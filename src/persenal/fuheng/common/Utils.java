package persenal.fuheng.common;
public class Utils {
    public static boolean matches(String font,String str){
        Pattern p = Pattern.compile(font);
        Matcher m = p.matcher(str);
        return m.matches();
    }
}
