
public class Vector3 {
public double x, y, z;
	
	public Vector3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
 
	public Vector3(Point a, Point b) {
		x = b.x - a.x;
		y = b.y - a.y;
		z = b.z - a.z;
	}
	
	public Vector3() {
		this.x = this.y = this.z = 0;
	}
	
	public double Length() {
		return Math.sqrt( (x*x) + (y*y) + (z*z) );
	}
	public void Normalize() {
		double len = this.Length();
		if(len == 0) return;
		this.x /= len;
		this.y /= len;
		this.z /= len;
	}
	
	public void Opposite() {
		this.x = -x;
		this.y = -y;
		this.z = -z;
	}
	
	public static Vector3 Add(Vector3 u, Vector3 v) {
		return new Vector3( u.x + v.x, u.y + v.y, u.z + v.z  );
	}
	public static Vector3 Substract(Vector3 u, Vector3 v) {
		return new Vector3( u.x - v.x, u.y - v.y, u.z - v.z  );
	}
	public static Vector3 Opposite(Vector3 v) {
		return new Vector3(-v.x, -v.y, -v.z);
	}
	
	public static Vector3 Multiply(Vector3 v, double a) {
		return new Vector3( a*v.x, a*v.y, a*v.z );
	}
	
	public static double dotProduct(Vector3 u, Vector3 v) {
		return ((u.x*v.x) + (u.y*v.y) + (u.z*v.z)); 
	}
	
	public static Vector3 crossProduct(Vector3 u, Vector3 v) {
		return new Vector3(
							(u.y*v.z)-(u.z*v.y),
							-(u.x*v.z)+(u.z*v.x),
							(u.x*v.y)-(u.y*v.x)
				);
	}
	
	public static Vector3 Copy(Vector3 v) {
		return new Vector3( v.x, v.y, v.z );
	}
	
}
