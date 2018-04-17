
public class Ray {
	public Point origin;
	public Vector3 normal;
	
	public Ray(Point a, Point b){
		origin = a;
		normal = new Vector3(a, b);
		normal.Normalize();
	}
	
	public Ray(Point origin, Vector3 normal) {
		this.origin = origin;
		this.normal = normal;
		this.normal.Normalize();
	}
	
	public double DistanceToPoint(Point p) {
		Vector3 origin_p = new Vector3(origin, p);
		return Vector3.crossProduct(normal, origin_p).Length()/normal.Length();
	}
}
