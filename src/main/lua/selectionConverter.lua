STRATEGIES = {
	stabilized = 2 ^ 0,
	recurring = 2 ^ 1
}

CONFIG_FILEPATH = (os.getenv("TMP") or os.getenv("TEMP")
	or error("Could not find a temporary directory.")
) .. "\\HookAnyText\\hex_config"

HAT_IS_RUNNING = true

HAT_MENU_ITEM = nil

-- main hex capture function
function convertHexSelection(threadObj)

	os.remove(CONFIG_FILEPATH)
	math.randomseed(os.time())

	local hexView = getMemoryViewForm().HexadecimalView
	local history = {}
	local recurringHistory = {}
	local cmdTitle = "HATCMD" .. string.format("%06d", math.random(1000000) - 1)
	local cmdHidden = false
	local handle = io.popen(
		"TITLE " .. cmdTitle .. " & "
			.. "java.exe -jar \"" .. getCheatEngineDir()
			.. "autorun\\HexToString.jar\" ",
		"w"
	)
	HAT_IS_RUNNING = true
	local selectionSize = 0
	local previousBytes = {}


	local sendText = function(cmdOrHex)
		handle:write(cmdOrHex .. "\n")
		handle:flush()
	end

	getMainForm().OnClose = function(sender)
		pcall(function()
			sendText(":exit")
			handle:close()
		end)
		closeCE()
	end

	HAT_MENU_ITEM.OnClick = function(sender)
		if HAT_IS_RUNNING then
			sendText(":focus")
		else
			createNativeThread(convertHexSelection)
			HAT_IS_RUNNING = true
		end
	end


	while true do

		sleep(REFRESH_DELAY)
		if checkConfigurationUpdates() then
			setConfiguration()
		end

		if not HAT_IS_RUNNING then
			break
		end

		if not cmdHidden then
			sendText(":hide " .. cmdTitle)
			cmdHidden = true
		end

		if hexView.hasSelection then
			selectionSize = hexView.SelectionStop-hexView.SelectionStart
			local bytes = getBytes(
				readBytes(hexView.SelectionStart, selectionSize + 1, true),
				history,
				recurringHistory
			)
			if bytes ~= nil
				and countDifferences({previousBytes, bytes})
					> getTableLength(bytes) * STABILIZATION_THRESHOLD
			then
				local s = ""
				for i = 1, getTableLength(bytes) do
					s = s .. string.format("%02x", bytes[i])
				end
				sendText(s)
				previousBytes = bytes
			end
		end

	end

end

-- determines whether it's worth converting the selection or not
-- returns nil if not
-- returns the array of bytes to use otherwise
function getBytes(selectionContent, history, recurringHistory)

	if selectionContent == nil then
		return nil
	end

	pushFirst(history, selectionContent, HISTORY_SIZE)
	local bytes = selectionContent
	if band(UPDATE_STRATEGY, STRATEGIES.recurring, 8) > 0 then
		bytes = constructHexFromHistory(history)
		pushFirst(recurringHistory, bytes, HISTORY_SIZE)
	end

	if band(UPDATE_STRATEGY, STRATEGIES.stabilized, 8) > 0 then
		local differences = 0
		if band(UPDATE_STRATEGY, STRATEGIES.recurring, 8) > 0 then
			differences = countDifferences(recurringHistory)
		else
			differences = countDifferences(history)
		end

		if differences > getTableLength(bytes) * STABILIZATION_THRESHOLD then
			return nil
		end
	end

	return bytes
end

-- gets the minimum size of an array in a list
function getMinSize(tables)
	if tables == nil or getTableLength(tables) == 0 then
		return 0
	end
	local minSize = getTableLength(tables[1]);
	for i = 2, getTableLength(tables) do
		if getTableLength(tables[i]) < minSize then
			minSize = getTableLength(tables[i])
		end
	end
	return minSize
end

-- count the number of different elements between arrays
function countDifferences(tables)
	local differenceCounter = 0;
	for i = 1, getTableLength(tables) - 1 do
		for j = 1, getMinSize(tables) do
			if tables[i][j] ~= tables[i + 1][j] then
				differenceCounter = differenceCounter + 1
			end
		end
		differenceCounter = differenceCounter
			+ math.abs(getTableLength(tables[i]) - getTableLength(tables[i+1]))
	end
	return differenceCounter
end

-- binary and on bigEndInd bits
function band(elt1, elt2, bigEndInd)
	local a = elt1 % (2 ^ (bigEndInd + 1))
	local b = elt2 % (2 ^ (bigEndInd + 1))
	local res = 0
	for i = 0, bigEndInd do
		local powerOfTwo = (2 ^ (bigEndInd - i))
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

-- constructs a byte array by saving the most frequent byte at a given position
-- using the whole history
function constructHexFromHistory(history)
	local res = {}
	for j = 1, getMinSize(history) do
		local bytesAtI = {}
		for i = 1, getTableLength(history) do
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

-- pushes an elements at the first place of an array
function pushFirst(array, elt, maxSize)
	table.insert(array, 1, elt)
	while getTableLength(array) > maxSize do
		table.remove(array, maxSize + 1)
	end
end

-- sets the value of a global variable from its name
-- taken from the official documentation
function setfield(f, v)
	local t = _G
	for w, d in (string.gfind or string.gmatch)(f, "([%w_]+)(.?)") do
		if d == "." then
			t[w] = t[w] or {}
			t = t[w]
		else
			t[w] = v
		end
	end
end

-- sets configuration variables
function setConfiguration()
	local f = nil
	local nbTries = 0
	while f == nil and nbTries < 30 do
		nbTries = nbTries + 1
		f = io.open(CONFIG_FILEPATH, "r")
		sleep(200)
	end
	if nbTries >= 50 and f == nil then
		error("Could not open temporary configuration update file.")
	end

	local keyValueCouples = f:read("*all")
	for k, v in string.gmatch(keyValueCouples, "([%w_]+)=([%w_]+)") do
		setfield(k, tonumber(v) or strtobool(v))
	end

	f:close()
	os.remove(CONFIG_FILEPATH)
	translateStrategy()
end

-- interprets "true" and "false" to true and false respectively
-- returns str if any other value
function strtobool(str)
	if str == "true" then
		return true
	elseif str == "false" then
		return false
	end
	return str
end

-- translates the name of an update strategy into a usable value
function translateStrategy()
	if UPDATE_STRATEGY == "basic" then
		HISTORY_SIZE = 1
		STABILIZATION_THRESHOLD = 0
		UPDATE_STRATEGY = STRATEGIES.stabilized
	elseif UPDATE_STRATEGY == "stabilized" then
		UPDATE_STRATEGY = STRATEGIES.stabilized
	elseif UPDATE_STRATEGY == "recurring" then
		UPDATE_STRATEGY = STRATEGIES.recurring
	elseif UPDATE_STRATEGY == "combined" then
		UPDATE_STRATEGY = STRATEGIES.stabilized + STRATEGIES.recurring
	end
end

-- checks for an update in configuration
function checkConfigurationUpdates()
	local f = io.open(CONFIG_FILEPATH,"r")
	if f ~= nil then
		f:close()
		return true
	else
		return false
	end
end

-- creates a menu item for HAT in CE's main form's "File" menu
function addHATMenuItem()
	local fileMenu = getMainForm().Menu.Items[0]
	HAT_MENU_ITEM = createMenuItem(fileMenu)
	HAT_MENU_ITEM.Caption = "Hook Any Text"
	fileMenu.insert(0, HAT_MENU_ITEM)
end

function getTableLength(t)
	if table.getn then
		return table.getn(t)
	else
		return #t
	end
end

addHATMenuItem()
createNativeThread(convertHexSelection)
