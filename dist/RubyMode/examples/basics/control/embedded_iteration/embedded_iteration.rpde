#
# Embedding Iteration.
#
# Embedding "for" structures allows repetition in two dimensions.
#

size(640, 360)
background(0)
no_stroke()

gridSize = 40

gridSize.step(width - gridSize, gridSize) do |x|
  gridSize.step(height - gridSize, gridSize) do |y|
    no_stroke()
    fill(255)
    rect(x-1, y-1, 3, 3)
    stroke(255, 50)
    line(x, y, width/2, height/2)
  end
end

