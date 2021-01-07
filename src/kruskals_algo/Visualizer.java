import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;

public class Visualizer {
	private final int width, length;
	private final List<String> sensor_network;
	private final List<LinkedList<String>> sensor_graph;
	private final LinkedList<String> mst_path;

	public Visualizer(int __width, int __length, List<String> __sensor_network,
			List<LinkedList<String>> __sensor_graph, LinkedList<String> __mst_path) {
		width = __width;
		length = __length;
		sensor_network = __sensor_network;
		sensor_graph = __sensor_graph;
		mst_path = __mst_path;
	}

	public void showGraph() {
		// Create window using JFrames
		JFrame frame = new JFrame("Sensor Graph");

		// setting closing action of JFrame
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create Graph
		DrawGraph graph = new DrawGraph(getWidth(), getLength(), getSensor_network(), getSensor_graph(), getMST_path());

		// add graph to JPanel
		frame.getContentPane().add(graph);
		// pack all contents into frame
		frame.pack();
		frame.setLocationByPlatform(true);
		
		// make frame visible
		frame.setVisible(true);
	}

	/**
	 * @return the sensor_network
	 */
	private List<String> getSensor_network() {
		return sensor_network;
	}

	/**
	 * @return the sensor_graph
	 */
	private List<LinkedList<String>> getSensor_graph() {
		return sensor_graph;
	}

	/**
	 * @return the width of graph
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @return the length of graph
	 */
	public int getLength() {
		return length;
	}

	/**
	 * @return the shortest_path
	 */
	public LinkedList<String> getMST_path() {
		return mst_path;
	}

}
