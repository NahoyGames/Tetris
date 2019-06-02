package launcher;


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

public class Launcher
{
	public static final String LAUNCHER_METADATA_LINK = "https://raw.githubusercontent.com/NahoyGames/Tetris/master/src/launcher/launcherMeta";
	public static final String LOCAL_VERSION_NAME = "MultiplayerTetris.jar";


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
			String[] launcherMetaData = getLauncherMetaData();
			System.out.println("The latest game version is: " + launcherMetaData[0]);

			statusLabel.setText("Downloading latest game build...");
			System.out.println("Attempting to download the latest game file at \"" + launcherMetaData[1] + "\"...");
			File localGameVersion = downloadGameVersion(launcherMetaData[1]);

			System.out.println("Successfully downloaded the latest game version!");

			statusLabel.setText("Game is ready to play!");
			frame.add(usernameField);
			((JButton)frame.getContentPane().add(new JButton("Launch Game"))).addActionListener((ActionEvent e) ->
			{
				try
				{
					System.out.println("Attempting to launch the game...");
					Runtime.getRuntime().exec("java -jar " + localGameVersion.getPath() + " " + launcherMetaData[2] + " " + usernameField.getText());
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
			System.exit(-1);
		}
	}


	private static String[] getLauncherMetaData() throws Exception
	{
		URL url = new URL(LAUNCHER_METADATA_LINK);

		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

		String version = in.readLine();
		String jarLink = in.readLine();
		String serverIp = in.readLine();

		in.close();

		return new String[] { version, jarLink, serverIp };
	}


	private static File downloadGameVersion(String url) throws Exception
	{
		InputStream in = new URL(url).openStream();

		Files.copy(in, Paths.get(LOCAL_VERSION_NAME), StandardCopyOption.REPLACE_EXISTING);

		return Paths.get(LOCAL_VERSION_NAME).toFile();
	}


	private static boolean launchGame()
	{
		try
		{

			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}
}
