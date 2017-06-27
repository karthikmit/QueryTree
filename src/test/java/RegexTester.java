import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by karthik on 27/06/17.
 */
public class RegexTester {

    @Test
    public void testRegexToFindIndex() {
        String in = "Item[1]";

        Pattern p = Pattern.compile("\\[(.*?)\\]");
        Matcher m = p.matcher(in);

        while(m.find()) {
            System.out.println(m.group(1));
        }
    }
}
