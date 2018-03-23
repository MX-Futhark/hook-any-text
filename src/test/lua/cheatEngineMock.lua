-- HOW TO: open a command prompt in the directory containing this file. Run:
--	 lua cheatEngineMock.lua ..\resources\tests\someTxtFile
-- Write two integers separated by a space to define a selection in this file.
-- Write "exit" to exit the REPL loop.
-- Assumes HookAnyText is built in the target directory with the name:
--	  HookAnyTest-someVersionNumber-jar-with-dependencies.jar


-- helpers

-- FIXME: Upon exiting, the error message is buried in infinite
-- "Line not found" messages, hence the force kill.
local exitProcess = function()
	os.execute("taskkill /f /t /im lua*>nul 2>nul")
end

local conversionLoopCoroutine

local resumeConversionLoopCoroutine = function()
	local ok, errorMsg = coroutine.resume(conversionLoopCoroutine)
	if not ok then
		print("Error in coroutine:")
		print(errorMsg)
		exitProcess()
	end
end

local split = function(inputstr)
	local t = {} ; i = 1
	for str in string.gmatch(inputstr, "([^%s]+)") do
		t[i] = str
		i = i + 1
	end
	return t
end


-- global mocks

local mockMemoryZone = io.open(arg[1], "r")
local mockMemoryZoneContent = mockMemoryZone:read("*all")

createNativeThread = function(f)
	conversionLoopCoroutine = coroutine.create(f)
	resumeConversionLoopCoroutine()
end

local memViewForm = {}
memViewForm.HexadecimalView = {}
getMemoryViewForm = function()
	return memViewForm
end

getMainForm = function()
	local form = {}
	form.Menu = {}
	form.Menu.Items = {}
	form.Menu.Items[0] = {}
	form.Menu.Items[0].insert = function() end

	return form
end

createMenuItem = function()
	return {}
end

readBytes = function(start, length)
	local textSelection = mockMemoryZoneContent:sub(start, start + length)
	local bytes = {}
	for i = 1, #textSelection do
		if i % 2 == 0 then
			bytes[i / 2] = tonumber(textSelection:sub(i - 1, i), 16)
		end
	end
	return bytes
end

getCheatEngineDir = function()
	return "..\\..\\..\\"
end

local clock = os.clock
sleep = function(n)
	if n == nil then
		n = 1000
	end
	local t0 = clock()
	while clock() - t0 <= math.ceil(n / 1000) do end
end

local hexViewMt = {}
local canReceiveInputs = false
hexViewMt.__index = function(table, key)
	-- The idea is to break the execution of the main conversion loop of
	-- selectionConverter.lua, which is run as a coroutine here instead of an
	-- actual native thread, to let the proto-REPL below accept inputs.
	if key == "hasSelection" then
		if canReceiveInputs then
			io.write("> ")
		else
			-- needed to avoid the current prompt being hidden
			os.execute("title cheatEngineMock")

			canReceiveInputs = true
		end
		coroutine.yield()
		return hasSelection
	else
		return nil
	end
end
setmetatable(memViewForm.HexadecimalView, hexViewMt)


-- proto-REPL

print("Please wait before entering inputs...")

os.execute(
	"echo F| xcopy ..\\..\\..\\target\\*jar-with-dependencies.jar "
	.. "..\\..\\..\\autorun\\HexToString.jar >nul"
)

dofile("..\\..\\main\\lua\\selectionConverter.lua")
sleep(2000)
resumeConversionLoopCoroutine()

local hexView = memViewForm.HexadecimalView
local command = io.read()
while command ~= "exit" do
	local elts = split(command)
	hexView.SelectionStart = tonumber(elts[1])
	hexView.SelectionStop = tonumber(elts[2])
	hasSelection = hexView.SelectionStart < hexView.SelectionStop
	resumeConversionLoopCoroutine()
	hasConfigBeenRead = true
	command = io.read()
end
mockMemoryZone:close()

os.execute("taskkill /f /fi \"WINDOWTITLE eq Hook Any Text\" >nul")
sleep(1000)
os.execute("rmdir /s /q ..\\..\\..\\autorun")
exitProcess()
