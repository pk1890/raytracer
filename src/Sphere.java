import java.awt.Color;

public class Sphere {
	public Point center;
	public double radius;
	public Color surfaceColor = Color.WHITE;
	public Material material = Material.DIFFUSE;
	public float lightIntensity = 0.0f;
	
	public Sphere(Point p, double rad, Color surfColor) {
		center = p;
		radius = rad;
		surfaceColor = surfColor; 
		this.material = Material.DIFFUSE;
	}
	
	public Sphere(Point p, double rad, float lightIntensity) {
		center = p;
		radius = rad;
		this.lightIntensity = lightIntensity;
		this.material = Material.EMMISION;
	}
	
	public Sphere(Point p, double rad, Material mat) {
		center = p;
		radius = rad;
		this.material = mat;
	}

	public boolean RayIntersects(Ray ray) {
		double dist = ray.DistanceToPoint(center);
		if(dist >= 0 && dist < radius) return true;
		else return false;
	}
	
	public Point GetClosestIntersectionPoint(Ray ray) {
		if(!RayIntersects(ray)) return new Point(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
		
		double x0, x1, y0, y1, z0, z1, a, b, c;
		
		
		// kula (x-x0)^2 + (y-y0)^2 + (z-z0)^2 = radius^2
		x0 = center.x;
		y0 = center.y;
		z0 = center.z;
		
		
		//prosta x = x1 + at; y = y1 + bt; z = z1 + ct
		x1 = ray.origin.x;
		y1 = ray.origin.y;
		z1 = ray.origin.z;
		
		a = ray.normal.x;
		b = ray.normal.y;
		c = ray.normal.z;
		
		//rozwi¹zanie równanie przeciêcia siê kuli z prost¹ ( równanie postaci At^2+Bt+C=0 )
		
		double A = (a*a) + (b*b) + (c*c);
		double B = 2*( (a*(x1-x0)) + (b*(y1-y0)) + (c*(z1-z0)) );
		double C = (x0*x0) + (y0*y0) + (z0*z0) + (x1*x1) + (y1*y1) + (z1*z1) - (radius * radius) -2*( (x0*x1) + (y0*y1 + (z0*z1)));
		
		double delta = (B*B) - (4*A*C);
		
		if (delta <= 0) return new Point(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY); // zabezpieczenia
		
		double sqrtD = Math.sqrt(delta);
		
		double t1 = (-B-sqrtD)/(2*A);
		double t2 = (-B+sqrtD)/(2*A);
		
		// wyliczam 2 punkty przeciêcia i biorê bli¿szy
		
		Point p1 = new Point( a*t1 + x1, b*t1 + y1, c*t1 + z1);
		Point p2 = new Point( a*t2 + x1, b*t2 + y1, c*t2 + z1);
		
		double d1 = new Vector3(p1, ray.origin).Length();
		double d2 = new Vector3(p2, ray.origin).Length();
		
		if(d1 < d2) return p1;
		return p2;
		
	}
	
	public double GetIntersectionDistance(Ray ray) {
		return new Vector3(this.GetClosestIntersectionPoint(ray), ray.origin).Length();
	}
	
	public Vector3 GetRayInpactNormalVector(Ray ray) {
		Vector3 norm = new Vector3(this.center, this.GetClosestIntersectionPoint(ray));
		norm.Normalize();
		return norm;
	}
	public float GetAngleToNormal(Ray ray) {
		Vector3 norm = this.GetRayInpactNormalVector(ray);
		return (float)Vector3.dotProduct(norm, Vector3.Opposite(ray.normal));
	}
	
	public Ray GetReflectedRay(Ray ray) {
		Point iPoint = this.GetClosestIntersectionPoint(ray);
		Vector3 norm = this.GetRayInpactNormalVector(ray);
		Vector3 diff = Vector3.Substract(norm, Vector3.Opposite(ray.normal));
		Vector3 newNormal = Vector3.Add(norm, diff);
		return new Ray(iPoint, newNormal);
	}
	
}


