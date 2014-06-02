package processing.mode.ruby;

import processing.app.Base;
import processing.app.Preferences;
import processing.app.RunnerListener;
import processing.app.exec.StreamRedirectThread;
import processing.core.PApplet;

import org.jruby.Ruby;
import org.jruby.embed.ScriptingContainer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import java.io.*;

public class RubyRunner {
  static public void run(File sourceFile) {
    ScriptingContainer container = new ScriptingContainer();
    container.setClassLoader(container.getClass().getClassLoader());
    container.runScriptlet(org.jruby.embed.PathType.ABSOLUTE, sourceFile.getAbsolutePath());
  }

  static public String join(String[] strings, String comma) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < strings.length; ++i) {
      if (i > 0)
        sb.append(comma);
      sb.append(strings[i]);
    }
    return sb.toString();
  }




  protected Process process;

  // Thread transferring remote output stream to our output stream
  protected Thread outThread = null;

  public void launchApplication(final String classPath, final String sourcePath) {
    final List<String> command = new ArrayList<String>();
    command.add(Base.getJavaPath());
    command.add("-cp");
    command.add(classPath);
    command.add(RubyRunner.class.getName());
    command.add(sourcePath);

    process = null;
    new Thread(new Runnable() {
        @Override
        public void run() {
          ProcessBuilder pb = new ProcessBuilder(command);
          try {
            process = pb.start();

            try {
              /*int result =*/ process.waitFor();
            } catch (InterruptedException ex) {
              System.err.println(ex);
            }
            System.err.println("Run finished");
          } catch (IOException ex) {
            System.err.println("IOException: " + ex);
          }
        }
      }).start();

    try {
      while (process == null)
        Thread.sleep(100);
    } catch (InterruptedException ex) {
      System.err.println(ex);
    }

    outThread = new StreamRedirectThread("JVM stdout Reader",
                                         process.getInputStream(),
                                         System.out);
    outThread.start();
    try {
      outThread.join();
    } catch (InterruptedException ex) {
      System.err.println(ex);
    }
  }





  public static void main(final String[] args) throws Exception {
    String sourceFileName = args[0];
    ScriptingContainer container = new ScriptingContainer();
    container.setClassLoader(container.getClass().getClassLoader());
    container.runScriptlet(org.jruby.embed.PathType.ABSOLUTE, sourceFileName);
  }
}
