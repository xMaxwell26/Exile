package com.koios.launch.loader.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.koios.launch.common.DesktopUtil;

public final class LinksActionListener implements ActionListener {

	private final Logger logger = Logger.getLogger(LinksActionListener.class);
	private final Map<String, String> sites = new HashMap<>();

	public LinksActionListener() {
		sites.put("Forums", "https://KoiosPS.com/forums/");
//		sites.put("Guides", "http://near-reality.org/ipb/index.php?/forum/19-guides/");
//		sites.put("Rules", "http://near-reality.org/ipb/index.php?/topic/35-near-reality-rules-04-17-14/");
//		sites.put("Highscores", "https://near-reality.org/skills.php");
//		sites.put("Donate", "https://near-reality.org/donate.php");
//		sites.put("Vote", "https://near-reality.org/vote.php");
//		sites.put("Report a bug", "https://bitbucket.org/modjacmob/nrpk/issues/new");
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		String address = sites.get(event.getActionCommand());
		if (address == null) {
			return;
		}

		try {
			DesktopUtil.openURL(new URL(address));
		} catch (Exception cause) {
			logger.error("An error occured whilst opening " + address, cause);
		}
	}

}