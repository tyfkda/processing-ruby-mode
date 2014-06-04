#
# Array.
#
# An array is a list of data. Each piece of data in an array
# is identified by an index number representing its position in
# the array. Arrays are zero based, which means that the first
# element in the array is [0], the second element is [1], and so on.
# In this example, an array named "coswav" is created and
# filled with the cosine values. This data is displayed three
# separate ways on the screen.
#

def setup()
  size(640, 360)
  @coswave = []
  width.times do |i|
    amount = map(i, 0, width, 0, PI)
    @coswave << cos(amount).abs
  end
  background(255)
  no_loop()
end

def draw()
  y1 = 0
  y2 = height/3
  0.step(width - 1, 2) do |i|
    stroke(@coswave[i]*255)
    line(i, y1, i, y2)
  end

  y1 = y2
  y2 = y1 + y1
  0.step(width - 1, 2) do |i|
    stroke(@coswave[i]*255 / 4)
    line(i, y1, i, y2)
  end

  y1 = y2
  y2 = height
  0.step(width - 1, 2) do |i|
    stroke(255 - @coswave[i]*255)
    line(i, y1, i, y2)
  end
end

