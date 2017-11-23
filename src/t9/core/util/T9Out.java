package t9.core.util;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import t9.core.esb.common.util.PropertiesUtil;
import t9.core.global.T9SysProps;
import t9.core.global.T9TokenConst;

/**
 * 系统输出包裹类
 * @author YZQ
 * @version 1.0
 * @date 2007-2-11
 */
public class T9Out {
  private static PrintStream out = System.out;
  public static boolean isDebug = getDebug();
  public static boolean getDebug() {
    String s = T9SysProps.getProp("DEBUG");
    if (T9Utility.isNullorEmpty(s) || !"1".equals(s)) {
      return false;
    }
    return true;
  }
  public static void debug(Object o) {
    if (isDebug) {
      out.println(o);
    }
  }
  private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
  /**
   * 构造函数
   * @param currClass
   */
  public static void init(String[] params) {
    
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    
    Throwable t = new Throwable();
    String fqnOfCallingClass = T9Out.class.getName();

    if(t == null) {
      return;
    }
    String tmpStr;

    synchronized(sw) {
      t.printStackTrace(pw);
      tmpStr = sw.toString();
      sw.getBuffer().setLength(0);
    }

    int ibegin = -1;
    int iend = -1;

    ibegin = tmpStr.lastIndexOf(fqnOfCallingClass);
    if(ibegin == -1) {
      return;
    }
    ibegin = tmpStr.indexOf(T9TokenConst.LINE_SEP, ibegin);
    if(ibegin == -1) {
      return;
    }
    ibegin += T9TokenConst.LINE_SEP_LEN;
    iend = tmpStr.indexOf(T9TokenConst.LINE_SEP, ibegin);
    if(iend == -1) {
      return;
    }
    ibegin = tmpStr.lastIndexOf("at ", iend);
    if(ibegin == -1)
      return;
    // Add 3 to skip "at ";
    ibegin += 3;
    //everything between is the requested stack item
    String fullInfo = tmpStr.substring(ibegin, iend);
    
    iend = fullInfo.lastIndexOf("(");
    if (iend < 0) {
      return;
    }
    ibegin = fullInfo.lastIndexOf(".", iend);
    if (ibegin < 0) {
      return;
    }
    params[0] = fullInfo.substring(0, ibegin);
    params[1] = fullInfo.substring(ibegin + 1, iend);
    ibegin = fullInfo.lastIndexOf(":");
    if (ibegin < 0) {
      return;
    }
    iend = fullInfo.lastIndexOf(")");
    if (iend < 0) {
      return;
    }
    params[2] = fullInfo.substring(ibegin + 1, iend);
  }


  /**
   * 不输出换行
   * @param outStr
   */
  public static void print(Object outStr) {
    
    String[] params = new String[3];    
    init(params);
    
    System.out.print("T9Out>>");
    System.out.print(outStr);
    System.out.print(">>");
    System.out.print(dateFormat.format(new Date()));
    System.out.print(" ");
    System.out.print(params[0]);
    System.out.print(".");
    System.out.print(params[1]);
    System.out.print(" ");
    System.out.print(params[2]);
  }
  /**
   * 输出换行
   * @param outStr
   */
  public static void println(Object outStr) {
    print(outStr);
    System.out.println();
  }
}
