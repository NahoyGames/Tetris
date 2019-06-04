package launcher;


import util.ParseUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Launcher
{
	public static final String LAUNCHER_METADATA_LINK = "https://docs.google.com/spreadsheets/d/1lC6AnZgw4LGute_icCiwuRZVOLTbTUJC7ckkOgK6MXU/export?format=tsv";
	public static final String LOCAL_VERSION_NAME = "tetris_game_latest_version.jar";


	public static void main(String[] args)
	{
		JFrame frame = new JFrame("Multiplayer Tetris Launcher");
		frame.setSize(300, 400);
		frame.setResizable(true);


		final JTextField usernameField = new JTextField("Username", 10);
		final JLabel statusLabel = new JLabel();
		frame.setContentPane(new JPanel()
		{
			{
				add(statusLabel);
			}
		});

		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


		try
		{
			statusLabel.setText("Connecting to server...");
			String[][] launcherMetaData = getLauncherMetaData();
			System.out.println("The latest game version is: " + launcherMetaData[3][1]);

			statusLabel.setText("Downloading latest game build...");
			System.out.println("Attempting to download the latest game file at \"" + launcherMetaData[4][1] + "\"...");
			File localGameVersion = downloadGameVersion(launcherMetaData[4][1]);

			System.out.println("Successfully downloaded the latest game version!");

			statusLabel.setText("Game is ready to play!");
			frame.add(usernameField);
			((JButton)frame.getContentPane().add(new JButton("Launch Game"))).addActionListener((ActionEvent e) ->
			{
				try
				{
					System.out.println("Attempting to launch the game...");
					// java -jar JAR_PATH SERVER_IP TCP UDP USERNAME
					Runtime.getRuntime().exec("java -jar "
							+ localGameVersion.getPath() + " "
							+ launcherMetaData[0][1] + " "
							+ launcherMetaData[1][1] + " "
							+ launcherMetaData[2][1] + " "
							+ usernameField.getText().replaceAll("\\s+", ""));
					System.exit(0);
				}
				catch (Exception ex)
				{
					System.out.println("Failed to launch the game...");
					statusLabel.setText("Failed to launch the game...");
				}
			});
			frame.getContentPane().setVisible(true);
			frame.setVisible(true);
		}
		catch (Exception e)
		{
			System.err.println("An error occurred while trying to fetch the latest game version...");
			e.printStackTrace();
			System.exit(-1);
		}
	}


	public static String[][] getLauncherMetaData() throws Exception
	{
		URL url = new URL(LAUNCHER_METADATA_LINK);

		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

		String[] rows = in.lines().toArray(String[]::new);
		//String file = in.lines().collect(Collectors.joining("\n"));

		in.close();

		return ParseUtil.parseTSV(rows);
	}


	private static File downloadGameVersion(String url) throws Exception
	{
		InputStream in = new URL(url).openStream();

		Files.copy(in, Paths.get(LOCAL_VERSION_NAME), StandardCopyOption.REPLACE_EXISTING);

		return Paths.get(LOCAL_VERSION_NAME).toFile();
	}
}
