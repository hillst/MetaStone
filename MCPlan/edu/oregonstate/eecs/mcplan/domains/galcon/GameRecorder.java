package edu.oregonstate.eecs.mcplan.domains.galcon;

import java.io.*;
import java.util.*;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import edu.oregonstate.eecs.mcplan.domains.galcon.graphics.Monitor;

public class GameRecorder {
	public static class FileContents {
		public ArrayList<GalconState> states;
		public ArrayList<GalconAction> actions;
		public FileContents(ArrayList<GalconState> s, ArrayList<GalconAction> a) {
			states = s;
			actions = a;
		}
	}
	
	public static FileContents readFile(ObjectInputStream objectIn)  {
		ArrayList<GalconState> stateList = new ArrayList<GalconState>();	
		ArrayList<GalconAction> actionList = new ArrayList<GalconAction>();
		try {
			GalconState s = (GalconState)objectIn.readObject();
			GalconAction a = (GalconAction)objectIn.readObject();
			do {
				stateList.add(s);
				actionList.add(a);
				s = (GalconState)objectIn.readObject();
				a = (GalconAction)objectIn.readObject();
			} while (s != null);
		} catch (EOFException e) {
			//EXCEPTIONS AS FLOW CONTROL IS COOL
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		return new FileContents(stateList, actionList);
	}
	
	public static void main(String[] args) {
		FileInputStream fileIn;
		ObjectInputStream objectIn;
		ArrayList<GalconState> states = null;
		ArrayList<GalconAction> actions = null;
		ArrayList<String> agents = null;
		
		String dir = "./";
		String file = "";
		
		JFileChooser chooser = new JFileChooser(dir);
	    FileNameExtensionFilter filter = new FileNameExtensionFilter(
	        "MCPlan replay files", "game");
	    chooser.setFileFilter(filter);
	    int returnVal = chooser.showOpenDialog(new JPanel());
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	    	file = chooser.getSelectedFile().getName();
	    } else {
	    	return;
	    }
	    
		try {
			fileIn = new FileInputStream(file);
			objectIn = new ObjectInputStream(fileIn);
			agents = (ArrayList<String>)objectIn.readObject();
			for(String s : agents) {
				System.out.println(s);
			}
			FileContents fc = GameRecorder.readFile(objectIn);
			states = fc.states;
			actions = fc.actions;
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		GalconState currentState = states.get(0);
		GalconAction currentAction = actions.get(0);
		try {
			Monitor.createMonitor(currentState.getMapWidth(), currentState.getMapHeight(), agents);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		Monitor.getInstance().showWindow(true);
		
		int max_frame = states.size();
		int last_index = -1;
		
		while (true) {
			//System.out.println("Frame #"+frame);
			double time = Monitor.getInstance().getPlaybackTime();
			int index = (int)(time*max_frame);
			
			if (index != last_index) {
				index = Math.max(Math.min(index, states.size()-1),0);
				currentState = states.get(index);
				currentAction = actions.get(index);
				Monitor.getInstance().updateMonitor(currentState);
				Monitor.getInstance().drawAction(currentAction, currentState);
				last_index = index;
				System.out.println(currentAction);
			} else {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					//????
				}
			}
		}
	}
}
