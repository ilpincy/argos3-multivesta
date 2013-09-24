--[[
		With this controller, a robot performs random walk with obstacle avoidance
		mimicking a gas particle. The robot keeps going straight until an obstacle is
		too close. When an obstacle is close enough, the robot simulates a "particle
		collision" and turns away from it. The turning direction is dictated
		by the angle at which the obstacle is located with respect to the robot.
  ]]



--[[ Constants ]]



--[[
		This parameter is to check whether is close enough to react to it. Remember that
		the proximity sensor readings decrease with the distance of the sense obstacle.
  ]]
DELTA = 0.001



--[[ Actual code ]]



--[[ Controller init ]]
function init()
	-- nothing to do
end

--[[ Controller step ]]
function step()
	-- We treat each proximity reading as a vector whose length is given by the value
	-- and whose angle is given by the angle corresponding to the reading
	-- First, we sum all these vectors into a variable called 'accum'
   accum = { x=0, y=0 }
   for i = 1,24 do
		-- This is the way to initialize a vector from length and angle
      local vec = {
			x = robot.proximity[i].value * math.cos(robot.proximity[i].angle),
			y = robot.proximity[i].value * math.sin(robot.proximity[i].angle)
		}
		-- Sum the 'vec' to the accumulator 'accum'
      accum.x = accum.x + vec.x
      accum.y = accum.y + vec.y
   end
	-- Now 'accum' is a vector that points to the direction of the closest obstacle
	-- OK, this is not always true. In fact, if two obstacles are located at symmetric
	-- positions around a robot, 'accum' will be zero. However, in practice these
	-- cases don't really count. Try it yourself to see that it's true!
	-- Get the angle of the accumulator
   angle = math.atan2(accum.y, accum.x)
	-- Get the length of the accumulator / 24 (in this way, we are sure that it is
	-- between 0 and 1 and setting a good value for delta is easy)
   length = math.sqrt(accum.x*accum.x + accum.y*accum.y) / 24
	-- Is the closest obstacle very close?
   if (length < DELTA) then
		-- No, keep going straight
      robot.wheels.set_velocity(5,5)
   else
		-- Yes, bounce away depending on the direction of the obstacle
      if angle > 0 then
			-- The obstacle is on the left, turn to the right
         robot.wheels.set_velocity(5,-2)
      else
			-- The obstacle is on the right, turn to the left
         robot.wheels.set_velocity(-2,5)
      end
   end
end

--[[ Controller reset ]]
function reset()
	-- nothing to do
end

--[[ Controller destroy ]]
function destroy()
	-- nothing to do
end
