package processing.mode.ruby;

import processing.app.Base;
import processing.app.Editor;
import processing.app.EditorState;
import processing.app.Mode;

import java.io.File;

public class RubyMode extends Mode {
  public RubyMode(Base base, File folder) {
    super(base, folder);
  }


  /**
   * Return the pretty/printable/menu name for this mode. This is separate from
   * the single word name of the folder that contains this mode. It could even
   * have spaces, though that might result in sheer madness or total mayhem.
   */
  @Override
  public String getTitle() {
    return "Ruby";
  }

  /**
   * Create a new editor associated with this mode.
   */
  @Override
  public Editor createEditor(Base base, String path, EditorState state) {
    return new RubyEditor(base, path, state, this);
  }

  /**
   * Returns the default extension for this editor setup. NOTE: no '.' at the
   * beginning, that causes problems!
   */
  @Override
  public String getDefaultExtension() {
    return "pde";
  }

  /**
   * Returns the module extension.
   */
  @Override
  public String getModuleExtension() {
    return "rb";
  }

  /**
   * Returns a String[] array of proper extensions.
   */
  @Override
  public String[] getExtensions() {
    return new String[] { getDefaultExtension(), getModuleExtension() };
  }

  /**
   * Get array of file/directory names that needn't be copied during "Save As".
   */
  @Override
  public String[] getIgnorable() {
    return new String[] {};
  }
}
