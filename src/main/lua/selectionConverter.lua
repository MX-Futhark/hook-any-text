function convertHexSelection(threadObj)

	local STRATEGIES = {
		stabilized = 2 ^ 0,
		recurring = 2 ^ 1
	}

	-- definition: Determines the encoding used to decode the selection.
	-- domain: among detect, sjis, utf16-be, utf16-le, utf8
	local ENCODING = "detect"

	-- definition: Filters converted strings deemed to be garbage.
	--     The lower the value, the more garbage there is.
	--     The higher the value, the higher the chance to filter actual text.
	-- domain: any positive or negative integer
	local STRICTNESS = 20

	-- definition: Determines at which proportion of the size of the selection
	--     the number of differences between the current content of the
	--     selection and that of the history is deemed low enough to convert
	--     the selection.
	-- domain: a real number between 0 and 1
	local STABILIZATION_THRESHOLD = 0.005

	-- definition: The number of ms to wait before capturing the selection.
	-- domain: a positive integer
	local REFRESH_DELAY = 50

	-- definition: The length of the array containing the previous selections.
	-- domain: a strictly positive integer
	local HISTORY_SIZE = 6

	-- definition: The strategy to use to deem the selection worthy of being
	--     converted.
	--     STRATEGIES.stabilized waits for the content of the selection to be
	--       stable enough, relative to STABILIZATION_THRESHOLD, to convert
	--       the selection. The whole history is used to compute the
	--       stabilization factor. The bigger the history, the longer the wait
	--       for converting the selection.
	--     STRATEGIES.recurring does not convert the selection directly, but
	--       an array of bytes constructed from the most common bytes at
	--       every position of the elements in the history.
	--     STRATEGIES.stabilized + STRATEGIES.recurring combines the two.
	--     NOTE: if HISTORY_SIZE == 1, the selection is immediatly converted
	--       every REFRESH_DELAY ms no matter the strategy.
	-- domain: STRATEGIES.stabilized, STRATEGIES.recurring or the sum of the two
	local UPDATE_STRATEGY = STRATEGIES.stabilized + STRATEGIES.recurring

	local hexView = getMemoryViewForm().HexadecimalView
	local history = {}
	local recurringHistory = {}
	local handle = io.popen(
		"java.exe -jar \"" .. getCheatEngineDir() ..
		"autorun\\HexToString.jar\" " ..
		"--encoding=" .. ENCODING .. " --strictness=" .. STRICTNESS,
		"w"
	)
	local selectionSize = 0

	-- determines whether it's worth converting the selection or not
	-- returns nil if not
	-- returns the array of bytes to use otherwise
	getBytes = function(selectionContent)

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

			if differences < table.getn(bytes) * STABILIZATION_THRESHOLD then
				return bytes
			else
				return nil
			end
		end

		return bytes
	end

	sendText = function(cmdOrHex)
		handle:write(cmdOrHex .. "\n")
		handle:flush()
	end

	getMainForm().OnClose = function(sender)
		pcall(function()
			sendText("exit")
			handle:close()
		end)
		closeCE()
	end

	local previousBytes = {}

	while true do

		if hexView.hasSelection then
			selectionSize = hexView.SelectionStop-hexView.SelectionStart
			local bytes = getBytes(readBytes(
				hexView.SelectionStart,
				selectionSize + 1,
				true
			))
			if bytes ~= nil
				and countDifferences({previousBytes, bytes})
					> table.getn(bytes) * STABILIZATION_THRESHOLD
			then
				local s = ""
				for i = 1, table.getn(bytes) do
					s = s .. string.format("%02x", bytes[i])
				end
				sendText(s)
				previousBytes = bytes
			end
		end

		sleep(REFRESH_DELAY)

	end

end

function getMinSize(tables)
	if tables == nil or table.getn(tables) == 0 then
		return 0
	end
	local minSize = table.getn(tables[1]);
	for i = 2, table.getn(tables) do
		if table.getn(tables[i]) < minSize then
			minSize = table.getn(tables[i])
		end
	end
	return minSize
end

function countDifferences(tables)
	local differenceCounter = 0;
	for i = 1, table.getn(tables) - 1 do
		for j = 1, getMinSize(tables) do
			if tables[i][j] ~= tables[i + 1][j] then
				differenceCounter = differenceCounter + 1
			end
		end
		differenceCounter = differenceCounter
			+ math.abs(table.getn(tables[i]) - table.getn(tables[i+1]))
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
		for i = 1, table.getn(history) do
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

function pushFirst(array, elt, maxSize)
	table.insert(array, 1, elt)
	if table.getn(array) > maxSize then
		table.remove(array, maxSize + 1)
	end
end


createNativeThread(convertHexSelection)