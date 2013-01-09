
public class Test {

	public static boolean checkSQL(String query){
		String s=query.replaceFirst("(\\s+)(select)", "select");
		if(s.startsWith("select"))
			return true;
		return false;
	}
	public static void main (String args[]){
		String s = "            select * from Product";
		boolean t =checkSQL(s);

		System.out.println(t);
//		s=s.replaceFirst("(\\s+)(select)", "select");
		System.out.println(s);
		t =checkSQL(s);

		System.out.println(t);
		
	}
}
