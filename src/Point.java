
public class Point{
	public double x, y, z;
	
	public Point(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Point() {
		this.x = this.y = this.z = 0;
	}
	
	public void AddVector(Vector3 v) {
		x += v.x;
		y += v.y;
		z += v.z;
	}
	
	
	public boolean Equals(Point p) {
		if(x == p.x && y == p.y && z == p.z) return true;
		else return false;
	}
	
	public void SubstractVector(Vector3 v) {
			this.AddVector(Vector3.Opposite(v));
	}
	
	public static Point AddVector(Point p, Vector3 v) {
		return new Point(p.x + v.x, p.y + v.y, p.z + v.z);
	}
	public static Point SubstractVector(Point p, Vector3 v) {
		return new Point(p.x - v.x, p.y - v.y, p.z - v.z);
	}
	public static Point Copy(Point p) {
		return new Point(p.x, p.y, p.z);
	}
	
}