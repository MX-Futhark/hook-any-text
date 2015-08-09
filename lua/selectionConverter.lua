function updateClipboard(threadObj)

	local hexView = getMemoryViewForm().HexadecimalView
	local previousBytes = {}
	local handle = io.popen(
        "java.exe -jar \"" .. getCheatEngineDir() ..
        "autorun\\HexToString.jar\" -sjis",
        "w"
    )
	local selectionSize = 0

    getMainForm().OnClose = function(sender)
        pcall(function()
            handle:write("exit")
            handle:close()
        end)
        closeCE()
    end

	while true do

		if hexView.hasSelection then

			selectionSize = hexView.SelectionStop-hexView.SelectionStart
			local bytes=readBytes(hexView.SelectionStart, selectionSize+1,true)
			if bytes ~= nil then

                local s = ""
                for i = 1, table.getn(bytes) do
                    s = s .. string.format("%02x", bytes[i])
                end
                handle:write(s .. "\n")
                handle:flush()
                previousBytes = bytes

			end

		end

        sleep(300)

	end

end


createNativeThread(updateClipboard)