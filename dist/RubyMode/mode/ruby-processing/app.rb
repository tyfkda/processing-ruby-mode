# version without embedded or online
# This class is a thin wrapper around Processing's PApplet.
# Most of the code here is for interfacing with Swing,
# web applets, going fullscreen and so on.

require 'java'
require 'ruby-processing/helper_methods'
require 'ruby-processing/library_loader'
#require 'ruby-processing/config'

Dir["#{Processing::CONFIG["PROCESSING_ROOT"]}/core/library/\*.jar"].each { |jar| require jar }

# Include some core processing classes that we'd like to use:
#   Removed: PGraphicsJava2D
%w(PApplet PConstants PFont PImage PShape PShapeOBJ PShapeSVG PStyle PGraphics PFont PVector PMatrix2D PMatrix3D).each do |klass|
  java_import "processing.core.#{klass}"
end

module Processing
  DEACTIVATE_RUN = '__DEACTIVATE_RUN__'

  # This is the main Ruby-Processing class, and is what you'll
  # inherit from when you create a sketch. This class can call
  # all of the methods available in Processing, and has two
  # mandatory methods, 'setup' and 'draw', both of which you
  # should define in your sketch. 'setup' will be called one
  # time when the sketch is first loaded, and 'draw' will be
  # called constantly, for every frame.
  class App < PApplet
    include Math
    include HelperMethods

    # Alias some methods for familiarity for Shoes coders.
    #attr_accessor :frame, :title
    alias_method :oval, :ellipse
    alias_method :stroke_width, :stroke_weight
    alias_method :rgb, :color
    alias_method :gray, :color
    field_reader :surface

    # When certain special methods get added to the sketch, we need to let
    # Processing call them by their expected Java names.
    def self.method_added(method_name) #:nodoc:
      # Watch the definition of these methods, to make sure
      # that Processing is able to call them during events.
      methods_to_alias = {
        mouse_pressed:  :mousePressed,
        mouse_dragged:  :mouseDragged,
        mouse_clicked:  :mouseClicked,
        mouse_moved:    :mouseMoved,
        mouse_released: :mouseReleased,
        key_pressed:    :keyPressed,
        key_released:   :keyReleased,
        key_typed:      :keyTyped
      }
      if methods_to_alias.keys.include?(method_name)
        alias_method methods_to_alias[method_name], method_name
      end
    end


    # Class methods that we should make available in the instance.
    [:map, :pow, :norm, :lerp, :second, :minute, :hour, :day, :month, :year,
    :sq, :constrain, :dist, :blend_color, :degrees, :radians, :mag, :println,
    :hex, :min, :max, :abs, :binary, :ceil, :nf, :nfc, :nfp, :nfs,
    :norm, :round, :trim, :unbinary, :unhex ].each do |meth|
      method = <<-EOS
      def #{meth}(*args)
      self.class.#{meth}(*args)
      end
      EOS
      eval method
    end

    # Handy getters and setters on the class go here:
    def self.sketch_class;  @sketch_class;        end
      @@full_screen = false
    def self.full_screen;   @@full_screen = true; end
    def full_screen?;       @@full_screen;        end

    def self.remove_sketch_class
      return unless @sketch_class
      classSymbol = @sketch_class.name.to_sym
      Object.class_eval { remove_const(classSymbol) }
      @sketch_class = nil
    end


    # Keep track of what inherits from the Processing::App, because we're going
    # to want to instantiate one.
    def self.inherited(subclass)
      super(subclass)
      @sketch_class = subclass
    end

    @@library_loader = LibraryLoader.new
    class << self
      def load_libraries(*args)
        @@library_loader.load_library(*args)
      end
      alias :load_library :load_libraries

      def library_loaded?(library_name)
        @@library_loader.library_loaded?(library_name)
      end

      def load_ruby_library(*args)
        @@library_loader.load_ruby_library(*args)
      end

      def load_java_library(*args)
        @@library_loader.load_java_library(*args)
      end
    end

    def library_loaded?(library_name)
      self.class.library_loaded?(library_name)
    end

    # When you make a new sketch, you pass in (optionally),
    # a width, height, x, y, title, and whether or not you want to
    # run in full-screen.
    #
    # This is a little different than Processing where height
    # and width are declared inside the setup method instead.
    def initialize(options={})
      super()
      $app = self
      proxy_java_fields
      #set_sketch_path #unless Processing.online?
      mix_proxy_into_inner_classes
      #@started = false

      java.lang.Thread.default_uncaught_exception_handler = proc do |thread, exception|
        $stderr.puts(exception.class.to_s)
        $stderr.puts(exception.message)
        $stderr.puts(exception.backtrace.collect { |trace| "\t" + trace })
        close
        $stderr.puts DEACTIVATE_RUN
      end

      # for the list of all available args, see
      # http://processing.org/reference/

      #args = []
      args = ARGV.dup

      @width, @height = options[:width], options[:height]

      if @@full_screen || options[:full_screen]
        @@full_screen = true
        args << "--present"
      end
      @render_mode  ||= JAVA2D
      if options[:x] && options[:y]
        args << "--location=#{options[:x]},#{options[:y]}"
      end

      title = options[:title] || File.basename(SKETCH_PATH).sub(/(\.(rpde|rb))$/, '')
      args << title  # run_sketch requires title, anyway.
      PApplet.run_sketch(args, self)
      surface.set_title(title)
    end

    def size(*args)
      w, h, mode       = *args
      @width           ||= w     unless @width
      @height          ||= h     unless @height
      @render_mode     ||= mode  unless @render_mode
      if [P3D, P2D].include? @render_mode
        # Include some opengl processing classes that we'd like to use:
        %w(FontTexture FrameBuffer LinePath LineStroker PGL PGraphics2D PGraphics3D PGraphicsOpenGL PShader PShapeOpenGL Texture).each do |klass|
          java_import "processing.opengl.#{klass}"
        end
      end
      super(*args)
    end

    # Make sure we set the size if we set it before we start the animation thread.
    def start
      self.size(@width, @height) if @width && @height
      super()
    end

    # Provide a loggable string to represent this sketch.
    def inspect
      "#<Processing::App:#{self.class}:#{@title}>"
    end


    # Close and shutter a running sketch. But don't exit.
    # @HACK seems to work with watch until we find a better
    # way of disposing of sketch window...
    def close
      surface.stopThread
      surface.setVisible(false) if surface.isStopped
      dispose
      $app = nil
    end

    def exit
      # Calling PApplet#exit kills own process, which causes slow restart.
      #super()
      self.close
      $stderr.puts DEACTIVATE_RUN
    end


    private

    # Mix the Processing::Proxy into any inner classes defined for the
    # sketch, attempting to mimic the behavior of Java's inner classes.
    def mix_proxy_into_inner_classes

      klass = Processing::App.sketch_class
      klass.constants.each do |name|
        const = klass.const_get name
        next if const.class != Class || const.to_s.match(/^Java::/)
        const.class_eval 'include Processing::Proxy'
      end
    end

  end # Processing::App


  # This module will get automatically mixed in to any inner class of
  # a Processing::App, in order to mimic Java's inner classes, which have
  # unfettered access to the methods defined in the surrounding class.
  module Proxy
    include Math

    # Generate the list of method names that we'd like to proxy for inner classes.
    # Nothing camelCased, nothing __internal__, just the Processing API.
    def self.desired_method_names(inner_class)
      bad_method = /__/    # Internal JRuby methods.
      unwanted = PApplet.superclass.instance_methods + Object.instance_methods
      unwanted -= ['width', 'height', 'cursor', 'create_image', 'background', 'size', 'resize']
      methods = Processing::App.public_instance_methods
      methods.reject {|m| unwanted.include?(m) || bad_method.match(m) || inner_class.method_defined?(m) }
    end


    # Proxy methods through to the sketch.
    def self.proxy_methods(inner_class)
      code = desired_method_names(inner_class).inject('') do |code, method|
        code << <<-EOS
          def #{method}(*args, &block)                # def rect(*args, &block)
            if block_given?                           #   if block_given?
              $app.send :'#{method}', *args, &block   #     $app.send(:rect, *args, &block)
            else                                      #   else
              $app.#{method} *args                    #     $app.rect *args
            end                                       #   end
          end                                         # end
        EOS
      end
      inner_class.class_eval(code)
    end


    # Proxy the sketch's constants on to the inner classes.
    def self.proxy_constants(inner_class)
      Processing::App.constants.each do |name|
        next if inner_class.const_defined?(name)
        inner_class.const_set(name, Processing::App.const_get(name))
      end
    end


    # Don't do all of the work unless we have an inner class that needs it.
    def self.included(inner_class)
      proxy_methods(inner_class)
      proxy_constants(inner_class)
    end

  end # Processing::Proxy

end # Processing
