import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

public class DrawGraph extends JPanel {
	private static final int BORDER_GAP = 30;
	private static final int LABEL_GAP = 20;
	private static final int PREF_W = 1280;
	private static final int PREF_H = 720;
	private static final Color GRAPH_BACKGROUND_COLOR = Color.LIGHT_GRAY;
	private static final Color GRAPH_COLOR = Color.DARK_GRAY;
	private static final Color GRAPH_LABEL_COLOR = Color.LIGHT_GRAY;
	private static final Color GRAPH_POINT_COLOR = Color.BLUE;
	private static final Color GRAPH_LINE_COLOR = Color.WHITE;
	private static final Color SHORTEST_PATH_COLOR = Color.YELLOW;
	private static final Stroke GRAPH_STROKE = new BasicStroke(3f);
	private static final int GRAPH_POINT_WIDTH = 12;
	private final int NO_OF_DIVISION_X;
	private final int NO_OF_DIVISION_Y;

	private final int width, length;
	private final List<String> sensor_network;
	private final List<LinkedList<String>> sensor_graph;
	private final LinkedList<String> mst_path;

	public DrawGraph(int __width, int __length, List<String> __sensor_network,
			List<LinkedList<String>> __sensor_graph, LinkedList<String> __mst_path) {
		width = __width;
		length = __length;
		sensor_network = __sensor_network;
		sensor_graph = __sensor_graph;
		mst_path = __mst_path;
		NO_OF_DIVISION_X = __width;
		NO_OF_DIVISION_Y = __length;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;

		List<String> __sensor_network = getSensor_network();
		List<LinkedList<String>> __sensor_graph = getSensor_graph();
		LinkedList<String> __mst_path = getMST_path();

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Calculate scaling factor
		double xScale = ((double) getWidth() - 2 * BORDER_GAP - LABEL_GAP) / (NO_OF_DIVISION_X - 1);
		double yScale = ((double) getHeight() - 2 * BORDER_GAP - LABEL_GAP) / NO_OF_DIVISION_Y;

		// draw Gray background
		g2d.setColor(GRAPH_BACKGROUND_COLOR);
		g2d.fillRect(BORDER_GAP, BORDER_GAP, getWidth() - (2 * BORDER_GAP), getHeight() - 2 * BORDER_GAP);
		g2d.setColor(GRAPH_LABEL_COLOR);

		// create x and y axes
		g2d.drawLine(BORDER_GAP, getHeight() - BORDER_GAP, BORDER_GAP, BORDER_GAP);
		g2d.drawLine(BORDER_GAP, getHeight() - BORDER_GAP, getWidth() - BORDER_GAP, getHeight() - BORDER_GAP);

		// Labeling for Y axis
		for (int i = 0; i < NO_OF_DIVISION_Y + 1; i++) {
			int x0 = BORDER_GAP;
			int x1 = GRAPH_POINT_WIDTH + BORDER_GAP;
			int y0 = getHeight() - ((i * (getHeight() - BORDER_GAP * 2 - LABEL_GAP)) / NO_OF_DIVISION_Y + BORDER_GAP);
			int y1 = y0;
			if (__sensor_network.size() > 0) {
				g2d.drawLine(BORDER_GAP + 1 + GRAPH_POINT_WIDTH, y0, getWidth() - BORDER_GAP, y1);
				g2d.setColor(GRAPH_LABEL_COLOR);
				String yLabel = ((int) ((getLength() * ((i * 1.0) / NO_OF_DIVISION_Y)) * 100)) / 100.0 + "";
				FontMetrics metrics = g2d.getFontMetrics();
				int labelWidth = metrics.stringWidth(yLabel);
				g2d.drawString(yLabel, x0 - labelWidth - 5, y0 + (metrics.getHeight() / 2) - 3);
			}
			g2d.drawLine(x0, y0, x1, y1);
			g2d.setColor(GRAPH_LINE_COLOR);
		}

		// Labeling for X axis
		for (int i = 0; i < NO_OF_DIVISION_X; i++) {
			int x0 = i * (getWidth() - BORDER_GAP * 2 - LABEL_GAP) / (NO_OF_DIVISION_X - 1) + BORDER_GAP;
			int x1 = x0;
			int y0 = getHeight() - BORDER_GAP;
			int y1 = y0 - GRAPH_POINT_WIDTH;
			if ((i % ((int) ((NO_OF_DIVISION_X / 20.0)) + 1)) == 0) {
				g2d.drawLine(x0, getHeight() - BORDER_GAP - 1 - GRAPH_POINT_WIDTH, x1, BORDER_GAP);
				g2d.setColor(GRAPH_LABEL_COLOR);
				String xLabel = ((int) ((getGraphWidth() * ((i * 1.0) / NO_OF_DIVISION_X)) * 100)) / 100.0 + "";
				FontMetrics metrics = g2d.getFontMetrics();
				int labelWidth = metrics.stringWidth(xLabel);
				g2d.drawString(xLabel, x0 - labelWidth / 2, y0 + metrics.getHeight() + 3);
			}
			g2d.drawLine(x0, y0, x1, y1);
			g2d.setColor(GRAPH_LINE_COLOR);
		}

		Stroke oldStroke = g2d.getStroke();
		g2d.setColor(GRAPH_COLOR);
		g2d.setStroke(GRAPH_STROKE);

		// draw lines from every node to its child node
		for (int i = 0; i < __sensor_network.size(); i++) {
			if (__sensor_graph.get(i).size() > 0) {
				for (int j = 0; j < __sensor_graph.get(i).size(); j++) {
					String[] node1 = __sensor_network.get(i).split(", ");
					String[] node2 = __sensor_network.get(j).split(", ");
					int x0 = (int) (Integer.parseInt(node1[0].replace("(", "")) * xScale) + BORDER_GAP;
					int y0 = (getHeight() - BORDER_GAP) - (int) (Integer.parseInt(node1[1].replace(")", "")) * yScale);
					int x1 = (int) (Integer.parseInt(node2[0].replace("(", "")) * xScale) + BORDER_GAP;
					int y1 = (getHeight() - BORDER_GAP)
							- (int) (Integer.parseInt(node2[1].replace(")", "")) * yScale);
					g2d.drawLine(x0, y0, x1, y1);
				}
			}
		}

		g2d.setStroke(oldStroke);
		g2d.setColor(SHORTEST_PATH_COLOR);
		
		// Drawing shortest path
		for (String edge: __mst_path) {
			String[] edge_nodes = edge.split(";");
			String[] node1 = edge_nodes[0].split(", ");
			String[] node2 = edge_nodes[1].split(", ");
			int x0 = (int) (Integer.parseInt(node1[0].replace("(", "")) * xScale) + BORDER_GAP;
			int y0 = (getHeight() - BORDER_GAP) - (int) (Integer.parseInt(node1[1].replace(")", "")) * yScale);
			int x1 = (int) (Integer.parseInt(node2[0].replace("(", "")) * xScale) + BORDER_GAP;
			int y1 = (getHeight() - BORDER_GAP)
					- (int) (Integer.parseInt(node2[1].replace(")", "")) * yScale);
			g2d.drawLine(x0, y0, x1, y1);
			g2d.drawLine(x0, y0, x1, y1);
		}

		g2d.setStroke(oldStroke);
		g2d.setColor(GRAPH_POINT_COLOR);

		// Marking Nodes on graph
		for (int i = 0; i < __sensor_network.size(); i++) {
			String[] node = __sensor_network.get(i).split(", ");
			int x = ((int) (Integer.parseInt(node[0].replace("(", "")) * xScale) - GRAPH_POINT_WIDTH / 2) + BORDER_GAP;
			int y = (getHeight() - BORDER_GAP) - (int) (Integer.parseInt(node[1].replace(")", "")) * yScale)
					- (GRAPH_POINT_WIDTH / 2);
			g2d.fillOval(x, y, GRAPH_POINT_WIDTH, GRAPH_POINT_WIDTH);
		}
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(PREF_W, PREF_H);
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
	 * @return the shortest_graph
	 */
	public LinkedList<String> getMST_path() {
		return mst_path;
	}

	/**
	 * @return the width
	 */
	public int getGraphWidth() {
		return width;
	}

	/**
	 * @return the length
	 */
	public int getLength() {
		return length;
	}

}
