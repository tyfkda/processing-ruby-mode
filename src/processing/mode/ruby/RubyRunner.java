package processing.mode.ruby;

import org.jruby.Ruby;
import org.jruby.embed.ScriptingContainer;

import java.io.File;

public class RubyRunner {
  static public void run(File sourceFile) {
    System.err.println("run: " + sourceFile);

    ScriptingContainer container = new ScriptingContainer();
    container.setClassLoader(container.getClass().getClassLoader());
    container.runScriptlet(org.jruby.embed.PathType.ABSOLUTE, sourceFile.getAbsolutePath());
  }
}
