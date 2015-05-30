package edu.oregonstate.eecs.mcplan.domains.galcon;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import edu.oregonstate.eecs.mcplan.Agent;
import edu.oregonstate.eecs.mcplan.Main;
import edu.oregonstate.eecs.mcplan.agents.DumbGalconAgent;
import edu.oregonstate.eecs.mcplan.domains.galcon.graphics.Monitor;

public class GalconAgentDebugger {
	public static void main(String[] args) {
		String file = pickFile();
		ArrayList<GalconState> states = readFile(file);
		
		GalconState firstState = states.get(0);
		try {
			Monitor.createMonitor(firstState.getMapWidth(), firstState.getMapHeight());
			Monitor.getInstance().updateMonitor(firstState);
		} catch (Exception e) {
			System.out.println("aaa");
		}

		List<Agent> agents = Main.selectAgents(1); //change me
		Agent agent = agents.get(0);
		
		while (true) {
			System.out.print("Input state number: ");
			int frame = Main.getIntegerInput();
			
			int player = -1;
			while (player != 0 && player != 1) {
				System.out.print("Input player number (0/1): ");
				player = Main.getIntegerInput();
			}
			
			GalconState state = getState(frame, player, states);
			Monitor.getInstance().updateMonitor(state);
			if (state == null) {
				System.out.println("Couldn't find frame in state list.");
				continue;
			}
			
			//Set up simulator w/ default settings.
			GalconSimulator sim = new GalconSimulator(400, 10, false, false, 0);
			GalconAction e = agent.selectAction(state, sim);
			System.out.println("Selected action: " + e.toString());
		}
	}
	
	private static String pickFile() {
		System.out.print("Enter replay filename: ");
		return getInput();
	}
	
	private static ArrayList<GalconState> readFile(String name) {
		FileInputStream fileIn = null;
		ObjectInputStream objectIn = null;
		ArrayList<GalconState> states = null;
		try {
			fileIn = new FileInputStream(name);
			objectIn = new ObjectInputStream(fileIn);
			ArrayList<String> agents = (ArrayList<String>)objectIn.readObject();
			GameRecorder.FileContents fc = GameRecorder.readFile(objectIn);
			states = fc.states;
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		return states;
	}
	
	private static GalconState getState(int frame, int player, ArrayList<GalconState> states) {
		for (GalconState s : states) {
			if (s.getCycle() == frame && s.getAgentTurn() == player) {
				return s;
			}
		}
		return null;
	}
	
	 private static String getInput() {
        String input = "";
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    System.in));
            input = in.readLine();
        } catch (IOException e) {
        }
        return input;
    }	
}
