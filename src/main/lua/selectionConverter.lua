local CheatEngine = _G

local STRATEGIES = {
	stabilized = 2 ^ 0,
	recurring = 2 ^ 1
}

local Helpers = (function ()

	--- @class Helpers
	-- Helper functions
	local Helpers = {}

	--- Cross-lua-versions expression evaluation
	-- @param expression The expression to evaluate
	-- @return The value of the evaluated expression, nil if invalid
	Helpers.evalExpression = function(expression)
		local functionBody = "return " .. expression
		local evaluatedFunction
		if load then
			evaluatedFunction = load(functionBody)
		else
			evaluatedFunction = loadstring(functionBody)
		end
		return evaluatedFunction()
	end

	--- binary and on a restricted number of bits
	-- @param elt1
	-- @param elt2
	-- @param n The number of bits to consider
	-- @return The resulting number
	Helpers.band = function(elt1, elt2, n)
		local a = elt1 % (2 ^ (n + 1))
		local b = elt2 % (2 ^ (n + 1))
		local res = 0
		for i = 0, n do
			local powerOfTwo = (2 ^ (n - i))
			if a / powerOfTwo >= 1 then
				a = a - powerOfTwo
				if b / powerOfTwo >= 1 then
					b = b - powerOfTwo
					res = res + powerOfTwo
				end
			end
		end
		return res
	end

	--- Pushes an elements at the first place of an array
	-- @param array
	-- @param elt
	-- @param maxSize Cuts of the end of the array if the operation causes
	--        its length to be greater than this value
	Helpers.unshift = function(array, elt, maxSize)
		table.insert(array, 1, elt)
		while Helpers.length(array) > maxSize do
			table.remove(array, maxSize + 1)
		end
	end

	--- Gets the minimum size of an array in a list
	-- @param tables An array of tables
	-- @return The minimum table size found
	Helpers.getMinSize = function(tables)
		if tables == nil or Helpers.length(tables) == 0 then
			return 0
		end
		local minSize = Helpers.length(tables[1]);
		for i = 2, Helpers.length(tables) do
			if Helpers.length(tables[i]) < minSize then
				minSize = Helpers.length(tables[i])
			end
		end
		return minSize
	end

	--- Counts the number of different elements between arrays
	-- @param tables An array of tables
	-- @return The number of differences found
	Helpers.countDifferences = function(tables)
		local differenceCounter = 0;
		for i = 1, Helpers.length(tables) - 1 do
			for j = 1, Helpers.getMinSize(tables) do
				if tables[i][j] ~= tables[i + 1][j] then
					differenceCounter = differenceCounter + 1
				end
			end
			differenceCounter = differenceCounter + math.abs(
				Helpers.length(tables[i]) - Helpers.length(tables[i+1])
			)
		end
		return differenceCounter
	end

	--- Converts an array of bytes to a formatted hexadecimal string
	-- @param bytes The array of bytes
	-- @return The resulting string
	Helpers.bytesToHexString = function(bytes)
		local s = ""
		for i = 1, Helpers.length(bytes) do
			s = s .. string.format("%02x", bytes[i])
		end
		return s
	end

	--- Cross-lua-versions table length
	-- @param t
	-- @return The length of the table
	Helpers.length = function(t)
		if table.getn then
			return table.getn(t)
		else
			return #t
		end
	end

	return Helpers
end)()

local Config = (function ()

	--- @class Config
	-- Hex config handling (See HexOptions on java side)
	local Config = {}

	local lastModificationTime

	Config.FILEPATH = (os.getenv("TMP") or os.getenv("TEMP")
		or error("Could not find a temporary directory.")
	) .. "\\HookAnyText\\hex_config"

	-- updated when the config file is read:
	Config.CLOSE_REQUESTED = false
	Config.STABILIZATION_THRESHOLD = 0
	Config.REFRESH_DELAY = 100
	Config.HISTORY_SIZE = 1
	Config.UPDATE_STRATEGY = 0
	-- NOTE:
	--  * index 0 = selection id
	--  * index 1 = selection start index
	--  * index 2 = selection end index
	--  * index 3 = whether the selection is active or not
	Config.HEX_SELECTIONS = { { -1, 0, 0, true } }

	--- Checks if a new config update exists
	-- @return A boolean
	Config.hasUpdate = function()
		local f = io.open(Config.FILEPATH, "r")
		if f ~= nil then
			f:close()
			return true
		else
			return false
		end
	end

	--- Sets configuration variables read from the config file
	Config.update = function ()

		Config.CLOSE_REQUESTED = false

		local f = nil
		local nbTries = 0
		while f == nil and nbTries < 30 do
			nbTries = nbTries + 1
			f = io.open(Config.FILEPATH, "r")
			CheatEngine.sleep(200)
		end
		if nbTries >= 50 and f == nil then
			error("Could not open temporary configuration update file.")
		end
	
		local keyValueCouples = f:read("*all")
		for k, v in string.gmatch(keyValueCouples, "([%w_]+)=([^\r\n]+)") do
			Config[k] = Helpers.evalExpression(v) or v
		end
	
		f:close()
		os.remove(Config.FILEPATH)
		Config.translateStrategy()
	end

	--- Translates the name of an update strategy into a usable value
	Config.translateStrategy = function ()
		if Config.UPDATE_STRATEGY == "basic" then
			Config.HISTORY_SIZE = 1
			Config.STABILIZATION_THRESHOLD = 0
			Config.UPDATE_STRATEGY = STRATEGIES.stabilized
		elseif Config.UPDATE_STRATEGY == "stabilized" then
			Config.UPDATE_STRATEGY = STRATEGIES.stabilized
		elseif Config.UPDATE_STRATEGY == "recurring" then
			Config.UPDATE_STRATEGY = STRATEGIES.recurring
		elseif Config.UPDATE_STRATEGY == "combined" then
			Config.UPDATE_STRATEGY =
				STRATEGIES.stabilized + STRATEGIES.recurring
		end
	end

	return Config
end)()

local Pipe = (function()

	--- @class Pipe
	-- lua-to-java communication methods
	local Pipe = {}

	local handle
	local cmdTitle

	--- Send a string though the pipe
	-- @param cmd
	local sendCommand = function(cmd)
		handle:write(cmd .. "\n")
		handle:flush()
	end

	--- Initializes the pipe
	Pipe.init = function()
		Config.CLOSE_REQUESTED = false
		os.remove(Config.FILEPATH)
		math.randomseed(os.time())
		cmdTitle = "HATCMD" .. string.format("%06d", math.random(1000000) - 1)
		handle = io.popen(
			"title " .. cmdTitle .. " & "
				.. "java.exe -jar \"" .. CheatEngine.getCheatEngineDir()
				.. "autorun\\HexToString.jar\" ",
			"w"
		)
	end

	--- Updates the content of a hex selection
	-- @param idToContent A table whose keys are selection ids and whose values
	--        are hex strings corresponding to the content of the selections
	Pipe.setSelectionsContent = function(idToContent)
		local args = ""
		for id, content in pairs(idToContent) do
			args = args .. " " .. id .. " " .. content
		end
		if args ~= "" then
			sendCommand("set-selections-content" .. args)
		end
	end

	--- Hides the terminal that appears when HAT starts
	Pipe.hideTerminal = function()
		sendCommand("hide-terminal " .. cmdTitle)
	end

	--- Requires the main window of HAT to be in focus
	Pipe.focusMainWindow = function()
		sendCommand("focus-main-window")
	end

	--- Notify the java side that the latest config has been read
	Pipe.acknowledgeConfigUpdate = function()
		sendCommand("acknownledge-config-update")
	end

	--- Changes the bounds of the currently active selection
	-- @param startIndex
	-- @param endIndex
	Pipe.updateActiveSelection = function(startIndex, endIndex)
		sendCommand("update-active-selection " .. startIndex .. " " .. endIndex)
	end

	--- Closes the pipe with a request for the java side to exit
	Pipe.close = function()
		sendCommand("exit")
		handle:close()
	end

	--- Checks if the pipe is effectively closed
	-- @return The state of the pipe as a boolean
	Pipe.isActive = function()
		return not Config.CLOSE_REQUESTED
	end

	return Pipe
end)()

local GUI = (function()

	--- @class GUI
	-- GUI helpers
	local GUI = {}

	--- Creates a menu item for HAT in CE's main form's "File" menu
	GUI.addHATMenuItem = function()
		local fileMenu = CheatEngine.getMainForm().Menu.Items[0]
		hatMenuItem = CheatEngine.createMenuItem(fileMenu)
		hatMenuItem.Caption = "Hook Any Text"
		hatMenuItem.OnClick = function()
			if Pipe.isActive() then
				Pipe.focusMainWindow()
			else
				CheatEngine.createNativeThread(convertHexSelections)
			end
		end
		fileMenu.insert(0, hatMenuItem)
	end

	return GUI
end)()

local HexSelection = (function()

	--- @class HexSelection
	-- Represents a selection (actual of virtual) in the hexview
	local HexSelection = {}
	HexSelection.__index = HexSelection

	local configSelectionsToHexSelections = {}

	--- Constructs a byte array by saving the most frequent byte at a given
	--  position using the whole history
	-- @param history
	-- @return The constructed table
	local constructHexFromHistory = function (history)
		local res = {}
		for j = 1, Helpers.getMinSize(history) do
			local bytesAtI = {}
			for i = 1, Helpers.length(history) do
				if bytesAtI[history[i][j]] == nil then
					bytesAtI[history[i][j]] = 1
				else
					bytesAtI[history[i][j]] = bytesAtI[history[i][j]] + 1
				end
			end
			local maxByte = nil
			for byte, count in pairs(bytesAtI) do
				if maxByte == nil or count > bytesAtI[maxByte] then
					maxByte = byte;
				end
			end
			res[j] = maxByte
		end
		return res
	end

	--- HexSelection constructor
	-- @param id
	-- @param startIndex
	-- @param endIndex
	-- @return A new HexSelection instance
	function HexSelection:create(id, startIndex, endIndex)
		local me = {}
		setmetatable(me, HexSelection)

		me.id = id
		me.startIndex = startIndex
		me.endIndex = endIndex
		me.history = {}
		me.recurringHistory = {}
		me.previousBytes = {}

		return me
	end

	--- Updates the indices of the selection
	-- @param startIndex
	-- @param endIndex
	function HexSelection:setIndices(startIndex, endIndex)
		if startIndex ~= nil then
			self.startIndex = startIndex
		end
		if endIndex ~= nil then
			self.endIndex = endIndex
		end
	end

	--- Determines whether it's worth converting the selection or not
	-- @return Nil if not, the array of bytes to use otherwise
	function HexSelection:getInterestingBytes()

		local selectionSize = self.endIndex - self.startIndex
		local selectionContent =
			CheatEngine.readBytes(self.startIndex, selectionSize + 1, true)

		if selectionContent == nil then
			return nil
		end

		Helpers.unshift(self.history, selectionContent, Config.HISTORY_SIZE)
		local bytes = selectionContent
		if Helpers.band(Config.UPDATE_STRATEGY, STRATEGIES.recurring, 8) > 0
			then

			bytes = constructHexFromHistory(self.history)
			Helpers.unshift(
				self.recurringHistory, bytes, Config.HISTORY_SIZE
			)
		end

		if Helpers.band(Config.UPDATE_STRATEGY, STRATEGIES.stabilized, 8) > 0
			then

			local differences = 0
			if Helpers.band(Config.UPDATE_STRATEGY, STRATEGIES.recurring, 8) > 0
				then

				differences = Helpers.countDifferences(self.recurringHistory)
			else
				differences = Helpers.countDifferences(self.history)
			end

			if differences >
				Helpers.length(bytes) * Config.STABILIZATION_THRESHOLD then

				return nil
			end
		end

		if Helpers.countDifferences({ self.previousBytes, bytes })
			<= Helpers.length(bytes) * Config.STABILIZATION_THRESHOLD then

			return nil
		end

		self.previousBytes = bytes

		return bytes
	end

	--- Constructs an array of HexSelection from a list of selection as
	--  formatted in the configuration
	-- @param configSelections
	-- @return An array of HexSelection
	HexSelection.getAllFromConfigFormat = function(configSelections)
		local newSelections = {}
		local res = {}

		for i = 1, Helpers.length(configSelections) do
			local s = configSelections[i]
			local currentSelection = configSelectionsToHexSelections[s[1]]
			if currentSelection then
				currentSelection:setIndices(s[2], s[3])
			else
				configSelectionsToHexSelections[s[1]] =
					HexSelection:create(s[1], s[2], s[3])
			end
			newSelections[s[1]] = configSelectionsToHexSelections[s[1]]
			res[i] = newSelections[s[1]]
		end

		configSelectionsToHexSelections = newSelections
		return res
	end

	--- Finds the active selection from a list of selection as formatted in the
	--  configuration
	-- @param configSelections
	-- @return Index of the active selection
	HexSelection.getActiveIndexFromConfigFormat = function(configSelections)
		local res = 1
		for i = 1, Helpers.length(configSelections) do
			if configSelections[i][4] then
				res = i
			end
		end
		return res
	end

	return HexSelection
end)()

--- Main hex capture function
function convertHexSelections()

	local hexView = CheatEngine.getMemoryViewForm().HexadecimalView
	local terminalHidden = false
	local previousSelectionStart
	local previousSelectionStop

	Pipe.init()

	while true do

		local configHasChanged = false

		CheatEngine.sleep(Config.REFRESH_DELAY)
		if Config.hasUpdate() then
			Config.update()
			configHasChanged = true
		end

		if not Pipe.isActive() then
			break
		end

		if configHasChanged then
			Pipe.acknowledgeConfigUpdate()
		end

		if not terminalHidden then
			Pipe.hideTerminal()
			terminalHidden = true
		end

		local selections =
			HexSelection.getAllFromConfigFormat(Config.HEX_SELECTIONS)
		local activeSelectionIndex =
			HexSelection.getActiveIndexFromConfigFormat(Config.HEX_SELECTIONS)
		local idToInterestingSelectionContent = {}

		for i = 1, Helpers.length(selections) do
			if i == activeSelectionIndex and hexView.hasSelection then
				selections[i]:setIndices(
					hexView.SelectionStart,
					hexView.SelectionStop
				)

				if configHasChanged
					or hexView.SelectionStart ~= previousSelectionStart
					or hexView.SelectionStop ~= previousSelectionStop then
		
					Pipe.updateActiveSelection(
						hexView.SelectionStart,
						hexView.SelectionStop
					)
					previousSelectionStart = hexView.SelectionStart
					previousSelectionStop = hexView.SelectionStop
				end
			end

			local interestingBytes = selections[i]:getInterestingBytes()
			if interestingBytes ~= nil then
				idToInterestingSelectionContent[selections[i].id] =
					Helpers.bytesToHexString(interestingBytes)
			end
		end

		Pipe.setSelectionsContent(idToInterestingSelectionContent)

	end

end


GUI.addHATMenuItem()
CheatEngine.getMainForm().OnClose = function()
	pcall(Pipe.close)
	CheatEngine.closeCE()
end
CheatEngine.createNativeThread(convertHexSelections)
