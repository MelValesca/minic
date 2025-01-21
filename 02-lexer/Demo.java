import java.io.FileReader; 
import language_demo.*;
public class Demo {
  public static void main(String[] args) {
    try {
      FileReader fr = new FileReader(args[0]);
      Lexer lexer = new Lexer(fr);
      Token t;
      do {
        t = lexer.next();
        System.out.println("["+t.getLine()+","+
          t.getPos()+"]"+t.getType()+" '"+
          t.getText()+"'");
      } while(t.getType() != Node.Type.TEnd);
    } catch(Exception e) {
        System.err.println(e.getMessage() + ".");
}}}

