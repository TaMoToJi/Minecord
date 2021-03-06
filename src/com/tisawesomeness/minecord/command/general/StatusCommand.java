package com.tisawesomeness.minecord.command.general;

import java.awt.Color;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.tisawesomeness.minecord.command.Command;
import com.tisawesomeness.minecord.util.MessageUtils;
import com.tisawesomeness.minecord.util.RequestUtils;

import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class StatusCommand extends Command {
	
	public CommandInfo getInfo() {
		return new CommandInfo(
			"status",
			"Checks the status of Mojang servers.",
			null,
			null,
			2000,
			false,
			false,
			true
		);
	}
	
	public Result run(String[] args, MessageReceivedEvent e) {
		
		//Request information from Mojang
		String request = RequestUtils.get("https://status.mojang.com/check");
		if (request == null) {
			return new Result(Outcome.ERROR, ":x: The Mojang API could not be reached.");
		}
		
		Color color = Color.GREEN;
		//Iterate over response sections
		ArrayList<String> responses = new ArrayList<String>();
		JSONArray status = new JSONArray(request);
		for (int i = 0; i < status.length(); i++) {
			
			//Fetch the response
			JSONObject json = status.getJSONObject(i);
			String[] names = JSONObject.getNames(json);
			String response = json.getString(names[0]);
			
			//Parse the response
			String output = ":x:";
			if (response.equals("green")) {
				output = ":white_check_mark:";
			} else if (response.equals("yellow")) {
				output = ":warning:";
				if (color != Color.RED) {
					color = Color.YELLOW;
				}
			} else {
				color = Color.RED;
			}
			
			responses.add(output);
		}
		
		//Build message
		String m = "**Minecraft:** " + responses.get(0) +
			"\n" + "**Skins:** " + responses.get(4) +
			"\n" + "**Textures:** " + responses.get(8) +
			"\n" + "**Session:** " + responses.get(1) +
			"\n" + "**Session Server:** " + responses.get(6) +
			"\n" + "**Accounts:** " + responses.get(2) +
			"\n" + "**Auth:** " + responses.get(3) +
			"\n" + "**Auth Server:** " + responses.get(5) +
			"\n" + "**Mojang:** " + responses.get(9) +
			"\n" + "**Mojang API:** " + responses.get(7);
		
		MessageEmbed me = MessageUtils.embedMessage("Minecraft Status", null, m, color);
		
		return new Result(Outcome.SUCCESS, me);
	}
	
}
