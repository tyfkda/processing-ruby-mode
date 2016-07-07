class EggRing
  def initialize(x, y, t, sp)
    @ovoid = Egg.new(x, y, t, sp)
    @circle = Ring.new()
    @circle.start(x, y - sp/2)
  end

  def transmit()
    @ovoid.wobble()
    @ovoid.display()
    @circle.grow()
    @circle.display()
    unless @circle.on
      @circle.on = true
    end
  end
end

