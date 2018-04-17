import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.util.Vector;

public class Camera {

	private int width;
	private int height;
	
	private double invwidth;
	private double invheight;
	
	public Point origin;
	public double angle_of_view;
	public Vector3 normal;
	
	public Color bgColor = Color.DARK_GRAY;
	
	private double biasX;
	private double biasZ;

	public BufferedImage canvas;
	
	
	public Camera(int w, int h, double aov) {
		/// Do dopracowania  - na razie przyjmujemy, ¿e kamera jest w (0,0,0) a normalna to [0,1,0]
		origin = new Point(0,0,0);
		angle_of_view = aov*Math.PI/180;
		double tang = Math.tan(angle_of_view/2);
		normal = new Vector3(0,1,0);
		normal.Normalize();
		
		height = h;
		width = w;
		
		invwidth = 1/(double)width;
		invheight = 1/(double)height;
		
		biasX = normal.Length() * tang;
		biasZ = normal.Length() * tang * height/(double)width;
		
		
		canvas = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
	}
	
	public void Render(Vector<Sphere> spheres) {
		for(int z = 0; z < height-1; z++){
			for(int x = 0; x < width-1; x++) {
				
				double xx = (2*(x+0.5)*invwidth - 1)*biasX; // Ustalanie, gdzie promieñ wychodz¹cy z oka przetnie obraz
				double zz = (2*(z+0.5)*invheight - 1)*biasZ;
				
				Point p = new Point(xx, 1, zz);
				
				Ray ray = new Ray(origin, p); //Puszczan promieñ przez ten punkt i oko
				
				Sphere intersectedSphere = GetFirstIntersectedSphere(spheres, ray);
				
				if(intersectedSphere != null) { // null = brak przeciêcia
					
					
						Color finalColor = Color.BLACK;
						switch(intersectedSphere.material) {
						case DIFFUSE:
							//finalColor = DiffuseShader(intersectedSphere, ray, spheres);
							finalColor = MixShader(Material.DIFFUSE, Material.GLOSSY, 0.8, intersectedSphere, ray, spheres);
							break;
							
						case EMMISION:
							finalColor = EmmisionShader();
							break;
						case GLOSSY:
							finalColor = GlossyShader(intersectedSphere, ray, spheres);
						}
					
						
						canvas.setRGB(x, z, finalColor.getRGB());
					//}
				}
				else {
					canvas.setRGB(x, z, Color.DARK_GRAY.getRGB());
				}
	
			}

		}
	}
	

	
	public Sphere GetFirstIntersectedSphere(Vector<Sphere> spheres, Ray ray) {
		double minimal_dist = Double.POSITIVE_INFINITY;
		
		Sphere intersectedSphere = null;
		boolean isIntersected = false;
		
		for(Sphere s : spheres) { // badam przeciêcia sie z kulami i wybieram tê która przecina promieñ jako pierwsza
			if(s.RayIntersects(ray)) {
				float angle_to_normal = s.GetAngleToNormal(ray); //sprawdzam, czy obiekt jest z przodu czy z ty³u kamery
				if(angle_to_normal > 0) {
					isIntersected = true;
					double dist = s.GetIntersectionDistance(ray);
					if(dist < minimal_dist) {
						intersectedSphere = s;
						minimal_dist = dist;
					}
				}
			}
		}
		return intersectedSphere;
	}
	

	public Color DiffuseShader(Sphere intersectedSphere, Ray ray, Vector<Sphere> spheres) {

		float angle_to_normal = intersectedSphere.GetAngleToNormal(ray); // k¹t pod jakim patrzysz na obiekt
		double intensity = angle_to_normal*255; // do efektu bia³ej odwódki ( coœ ala efekt freslena)
		double finalBrightness = 0;
		Point iPoint = intersectedSphere.GetClosestIntersectionPoint(ray);
		
		for(Sphere s : spheres) { // wysy³am odbity promieñ do ka¿dego ze œwiate³
			if(s.material == Material.EMMISION) {
				Ray lightRay = new Ray(intersectedSphere.GetClosestIntersectionPoint(ray), s.center);
				double angle = Vector3.dotProduct(lightRay.normal, new Vector3(intersectedSphere.center, iPoint)); // k¹t padania œwiat³a na powierzchnie
				if( angle > 0) { // jeœli angle < 0 to znaczy ¿e k¹t padanie œwiat³a jest > 90 stopni czyli œwiat³o wychodzi z kuli czyli ignorujemy
					if(GetFirstIntersectedSphere(spheres, lightRay) == s)
						finalBrightness += s.lightIntensity * Math.sqrt(angle); // jeœli pierwsze padnie na œwiat³o to zwiêksz jasnoœæ a im mniejszy k¹t padania œwiat³a tym bardziej
					
				}
			}
		}
		
		if(finalBrightness > 1) finalBrightness = 1;
		
		Color icolor = intersectedSphere.surfaceColor;
		float[]hsbvals = new float[3];
		Color.RGBtoHSB(icolor.getRed(), icolor.getGreen(), icolor.getBlue(), hsbvals);
		Color finalColor = Color.getHSBColor(hsbvals[0], hsbvals[1]*(float)Math.sqrt(angle_to_normal), hsbvals[2]*((float)finalBrightness));
		
		return finalColor;
	}
	
	public Color EmmisionShader() {
		return Color.WHITE;
	}
	
	public Color GlossyShader(Sphere intersectedSphere, Ray ray, Vector<Sphere> spheres) {
		float angle_to_normal = intersectedSphere.GetAngleToNormal(ray); // k¹t pod jakim patrzysz na obiekt
		double intensity = angle_to_normal*255; // do efektu bia³ej odwódki ( coœ ala efekt freslena)
		double finalBrightness = 0;
		Ray reflectedRay = intersectedSphere.GetReflectedRay(ray);
		
		Color finalColor = Color.BLACK;
		
		Sphere iSphere = GetFirstIntersectedSphere(spheres, reflectedRay);
		if(iSphere != null) {
			switch(iSphere.material) {
			case DIFFUSE:
				finalColor = DiffuseShader(iSphere, reflectedRay, spheres);
				break;
			case EMMISION:
				finalColor = EmmisionShader();
				break;
			case GLOSSY:
				finalColor = GlossyShader(iSphere, reflectedRay, spheres);
				break;
			}
		}
		else finalColor =  bgColor;
		
		float[]hsbvals = new float[3];
		Color.RGBtoHSB(finalColor.getRed(), finalColor.getGreen(), finalColor.getBlue(), hsbvals);
		finalColor = Color.getHSBColor(hsbvals[0], hsbvals[1]*(float)Math.sqrt(angle_to_normal), hsbvals[2]);
		
		return finalColor;
	}
	public Color MixShader(Material s1, Material s2, double factor, Sphere intersectedSphere, Ray ray, Vector<Sphere> spheres) {
		Color c1, c2;
		c1 = c2 = Color.BLACK;
		switch(s1) {
		case DIFFUSE:
			c1 = DiffuseShader(intersectedSphere, ray, spheres);
			break;
		case EMMISION:
			c1 = EmmisionShader();
			break;
		case GLOSSY:
			c1 = GlossyShader(intersectedSphere, ray, spheres);
			break;
		}
		
		switch(s2) {
		case DIFFUSE:
			c2 = DiffuseShader(intersectedSphere, ray, spheres);
			break;
		case EMMISION:
			c2 = EmmisionShader();
			break;
		case GLOSSY:
			c2 = GlossyShader(intersectedSphere, ray, spheres);
			break;
		}
		
		Color finalColor = blend(c1, c2, factor);
		
		return finalColor;
	}
	
	public static Color blend (Color color1, Color color2, double ratio)
	  {
	    float r  = (float) ratio;
	    float ir = (float) 1.0 - r;

	    float rgb1[] = new float[3];
	    float rgb2[] = new float[3];    

	    color1.getColorComponents (rgb1);
	    color2.getColorComponents (rgb2);    

	    Color color = new Color (rgb1[0] * r + rgb2[0] * ir, 
	                             rgb1[1] * r + rgb2[1] * ir, 
	                             rgb1[2] * r + rgb2[2] * ir);
	    
	    return color;
	  }
	
	
}
