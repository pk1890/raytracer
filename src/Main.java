

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.color.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;


public class Main {

	public static void main(String[] args) throws IOException {
		JFrame frame = new JFrame();
		frame.setLayout(new FlowLayout());
		frame.setSize(600, 450);
		
		Point p = new Point(1, 5 ,0);
		
		Vector<Sphere> spheres = new Vector<Sphere>();
		
		spheres.add(new Sphere(p, 1, Color.BLUE));
		p = new Point(0, 10 , 1);
		spheres.add(new Sphere(p, 1, Color.RED));
		p = new Point(0, 11 , 0.5);
		spheres.add(new Sphere(p, 1.2, Color.YELLOW));
		//spheres.add(new Sphere(p, 1.2, Material.GLOSSY));
		p = new Point(0, 5 , 1);
		spheres.add(new Sphere(p, 0.1, 1));
		Camera cam = new Camera(600, 400, 60);
		JLabel label = new JLabel();
		frame.add(label);
		label.setIcon(new ImageIcon(cam.canvas));
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() 
			{
				cam.Render(spheres);
				label.setIcon(new ImageIcon(cam.canvas));
				spheres.elementAt(0).center.x-=0.01;
				spheres.elementAt(1).center.y-=0.06;
				spheres.elementAt(2).center.z-=0.02;
				spheres.elementAt(3).center.x += 0.03;
			}
		}, 0, 10);
		
		
		
		
		
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

}
