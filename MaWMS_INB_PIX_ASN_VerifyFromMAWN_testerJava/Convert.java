import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public class Convert {

	public static String concatPoQty(String poLine, String qty) {
		
		//Make sure the qty is an integer and not a double
		String intValue = convertFromDoubleToInteger(qty);
		
		return poLine+"|"+intValue;
	}
	public static String convertToLocalTime(String dateString) throws ParseException {
		String pattern1    = "yyyy-MM-dd'T'HH:mm:ss";
//		String pattern2    = "yyyyMMddHHmmssZ";  //Z would include the timezone diff
		String pattern2    = "yyyyMMddHHmmss";

		SimpleDateFormat gmtFormat = new SimpleDateFormat(pattern1);
		SimpleDateFormat outFormat = new SimpleDateFormat(pattern2);

		Date date = gmtFormat.parse(dateString);
		gmtFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		date = gmtFormat.parse(dateString);

		System.out.println("GMT time: "+dateString);
		return outFormat.format(date);
	}
	public static String convertFromDoubleToInteger(String value ) {
		
		double doubleValue = Double.parseDouble(value);
		int intValue = (int) doubleValue;
		return String.valueOf(intValue);
	}

	public static String existOnManhattanMsg(String[] manhattanPoLines, String poLineSeq) {
		
		String status="no"; //means message is not a dup and cannot proceed further processing
		for(String po:manhattanPoLines) {
			if(poLineSeq.contains(po)) {
				status="yes";
				break;
			}
		}
		
		return status;
	}

	public static String getValue(String[] manhattanPoLines, String poLineSeq, String poValue, String manhattanValue, String[] xref) throws Exception {
		
		String status = existOnManhattanMsg(manhattanPoLines, poLineSeq);
		
		if(status.contains("yes")){
			return getNewValue(xref, poLineSeq, poValue);
		}
//		return "awgData "+poValue;
		return poValue;
	}
	private static String getNewValue(String[] xref, String poLineSeq, String poValue) throws Exception {
		
		HashMap<String,String> hash = new HashMap<String,String>();
		
		List<String> temp = new ArrayList<String>();
		String[] split;
		for (String x:xref){
			temp = splitValue("|", x);
			hash.put(temp.get(0), temp.get(1));
		}
		
		if(hash.containsKey(poLineSeq)) {
			if(hash.get(poLineSeq).contains("NoManhattanDataUsePoQty"))
//     			return "NoManhattanDataUsePoQty - use awgData "+poValue;
 			    return poValue;
			else
//			    return "manhattanData "+hash.get(poLineSeq);
		        return hash.get(poLineSeq);
		}
		
//		return "awgData "+poValue;
		return poValue;
	}
	private static ArrayList<String> splitValue(String sep, String original) throws Exception{

		   if (sep == null || sep.equals("") || original == null)
		      throw new IllegalArgumentException("null or empty String");
		   
		   ArrayList<String>  result = new ArrayList<String> ();
		   int oldpos = 0;
		   int pos;
		   int sepLength = sep.length();
		   String substr="";
		   
		   try{
			   while ((pos = original.toUpperCase().indexOf(sep, oldpos)) >= 0)
			   {
			   	  substr = original.substring(oldpos, pos);
			      if (substr.startsWith("\n"))
			         result.add(original.substring(oldpos + 1, pos));
			      else
			         result.add(substr);
			      oldpos = pos + sepLength;
			   }
		
			   if (original.substring(oldpos).toUpperCase().startsWith("\n"))
			      result.add(original.substring(oldpos + 1));
			   else
			      result.add(original.substring(oldpos));
		   }catch(Exception e){
			   System.out.println("TESTING ERROR "+original+"  "+substr);
		   }
		   return result;
		}
}
