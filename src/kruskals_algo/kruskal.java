
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class kruskal {

	static LinkedList<String> nodes = new LinkedList<String>();
	static List<LinkedList<String>> sensors = new LinkedList<LinkedList<String>>();
	static LinkedList<String> shotest = new LinkedList<String>();
	static double cost = 0;

	private static int getRandom(int max) {
		max--;
		Random r = new Random();
		return r.nextInt((max) + 1);
	}

	private static boolean bfs(List<LinkedList<String>> sensors, int n, LinkedList<String> nodes) {
		boolean connected = false;
		LinkedList<String> visited = new LinkedList<String>();
		LinkedList<String> queue = new LinkedList<String>();
		int index = getRandom(n);
		String rootnode = nodes.get(index);
		queue.add(rootnode);
		while (queue.size() != 0) {
			int current_node = index;
			if (visited.size() != 0) {
				current_node = nodes.indexOf(queue.get(0));
			}
			LinkedList<String> child = sensors.get(current_node);
			for (int i = 0; i < child.size(); i++) {
				if (!queue.contains(child) && !visited.contains(child)) {
					queue.add(child.get(i));
				}
			}
			visited.add(nodes.get(current_node));
			queue.remove(0);
			if (visited.size() == nodes.size()) {
				connected = true;
				break;
			}
		}

		return connected;
	}

	private static boolean isConnected(LinkedList<String> discovered_edges) {
		boolean result = false;
		LinkedList<String> queue = new LinkedList<>();
		LinkedList<String> visited_nodes = new LinkedList<>();
		LinkedList<String> _discovered_nodes = new LinkedList<>();
		if (discovered_edges.size() > 0) {
			String current_node = discovered_edges.get(0).split(";")[0];
			queue.add(current_node);
			_discovered_nodes.add(current_node);
			while (queue.size() > 0) {
				if (current_node == null)
					current_node = queue.get(0);
				visited_nodes.add(current_node);
				queue.removeFirst();
				for (String edge : discovered_edges) {
					String[] edge_nodes = edge.split(";");
					if (edge_nodes[0].equals(current_node) || edge_nodes[1].equals(current_node)) {
						if (edge_nodes[0].equals(current_node) && !queue.contains(edge_nodes[1])
								&& !visited_nodes.contains(edge_nodes[1])
								&& !_discovered_nodes.contains(edge_nodes[1])) {
							queue.add(edge_nodes[1]);
							_discovered_nodes.add(edge_nodes[1]);
						}

						if (edge_nodes[1].equals(current_node) && !queue.contains(edge_nodes[0])
								&& !visited_nodes.contains(edge_nodes[0])
								&& !_discovered_nodes.contains(edge_nodes[0])) {
							queue.add(edge_nodes[0]);
							_discovered_nodes.add(edge_nodes[0]);
						}
					}
				}

				if (nodes.size() == _discovered_nodes.size()) {
					result = true;
					break;
				}
				current_node = null;
			}
		}
		return result;
	}

	private static String get_next_edge(String next_min_edge, Map<String, Double> edge_cost,
			LinkedList<String> dedges) {
		String next_edge = null;
		double last_edge_cost = 0.0;
		if (next_min_edge != "") {
			last_edge_cost = edge_cost.get(next_min_edge);
		}
		double min_value = Double.MAX_VALUE;
		for (String key : edge_cost.keySet()) {
			String[] reversed_keys = key.split(";");
			String reversed_key = reversed_keys[1] + ";" + reversed_keys[0];
			if (edge_cost.get(key) < min_value && edge_cost.get(key) >= last_edge_cost && !isCyclic(dedges, key)
					&& !dedges.contains(key) && !dedges.contains(reversed_key)) {
				next_edge = key;
				min_value = edge_cost.get(key);
			}
		}
		return next_edge;
	}

	private static boolean get_union(LinkedList<String> edge_list1, LinkedList<String> edge_list2) {
		boolean result = false;
		for (String edge : edge_list1) {
			for (String edge2 : edge_list2) {
				if (edge == edge2) {
					result = true;
					break;
				}
			}
		}
		return result;
	}

	private static boolean isCyclic(LinkedList<String> discovered_edges, String next_edge) {
		boolean is_cyclic = false;
		LinkedList<String> _discovered_edges = new LinkedList<>();
		LinkedList<String> discovered_nodes = new LinkedList<>();
		_discovered_edges.addAll(discovered_edges);
		_discovered_edges.add(next_edge);
		boolean result = false;
		for (String edge : _discovered_edges) {
			String[] edge_nodes = edge.split(";");
			if (discovered_nodes.contains(edge_nodes[0]) && discovered_nodes.contains(edge_nodes[1])) {
				// possibility of cyclic
				LinkedList<String> edge_list1 = getconnected(edge_nodes[0], edge, _discovered_edges);
				LinkedList<String> edge_list2 = getconnected(edge_nodes[1], edge, _discovered_edges);
				is_cyclic = get_union(edge_list1, edge_list2);
				if (is_cyclic) {
					result = true;
					break;
				}
			} else {
				if (!discovered_nodes.contains(edge_nodes[0])) {
					discovered_nodes.add(edge_nodes[0]);
				}
				if (!discovered_nodes.contains(edge_nodes[1])) {
					discovered_nodes.add(edge_nodes[1]);
				}
			}
		}
		return result;
	}

	private static LinkedList<String> kruskal() {
		LinkedList<String> discovered_edges = new LinkedList<>();
		Map<String, Double> edge_cost = new HashMap<String, Double>();
		double total_energy = 0.0;
		for (int i = 0; i < nodes.size(); i++) {
			for (int j = 0; j < sensors.get(i).size(); j++) {
				edge_cost.put(nodes.get(i) + ";" + sensors.get(i).get(j), caledge(nodes.get(i), sensors.get(i).get(j)));
			}
		}
		String next_min_edge = "";
		while (!isConnected(discovered_edges)) {
			next_min_edge = get_next_edge(next_min_edge, edge_cost, discovered_edges);
			discovered_edges.add(next_min_edge);
		}
		printMST(discovered_edges);
		for (String key: edge_cost.keySet()) {
			total_energy += edge_cost.get(key);
		}
		System.out.println("Total energy cost is: ");
		System.out.println(total_energy);
		return discovered_edges;
	}

	private static void printMST(LinkedList<String> dedges) {
		System.out.println("\nMST graph: \n");
		for (String edge : dedges) {
			System.out.println(edge.replace(";", "-->"));
			System.out.println("");

		}
	}

	private static float distance(String node1, String node2) {
		String[] tpoint = node1.split(",");
		int x0 = Integer.parseInt(tpoint[0].replace("(", "").replace(" ", ""));
		int y0 = Integer.parseInt(tpoint[1].replace(")", "").replace(" ", ""));
		tpoint = node2.split(",");
		int x1 = Integer.parseInt(tpoint[0].replace("(", "").replace(" ", ""));
		int y1 = Integer.parseInt(tpoint[1].replace(")", "").replace(" ", ""));
		return (float) Math.sqrt(Math.pow(x1 - x0, 2) + Math.pow(y1 - y0, 2));
	}

	private static double caledge(String node1, String node2) {
		double l = distance(node1, node2);
		int k = 3200;
		double elec = 100 * Math.pow(10, -9);
		double eamp = 100 * Math.pow(10, -12);
		double st = (elec * k) + (eamp * k * Math.pow(l, 2));
		double rc = (elec * k);
		return st + rc;
	}

	private static LinkedList<String> getconnected(String current_node, String current_edge,
			LinkedList<String> discovered_edges) {
		LinkedList<String> queue = new LinkedList<>();
		LinkedList<String> visited_nodes = new LinkedList<>();
		LinkedList<String> connected_edges = new LinkedList<>();
		queue.add(current_node);
		while (queue.size() > 0) {
			if (current_node == null)
				current_node = queue.get(0);
			visited_nodes.add(current_node);
			queue.removeFirst();
			for (String edge : discovered_edges) {
				String[] edge_nodes = edge.split(";");
				if (edge_nodes[0].equals(current_node) || edge_nodes[1].equals(current_node)) {
					if (!connected_edges.contains(edge) && edge != current_edge) {
						connected_edges.add(edge);
						if (!queue.contains(edge_nodes[1]) && !visited_nodes.contains(edge_nodes[1])) {
							queue.add(edge_nodes[1]);
						}
						if (!queue.contains(edge_nodes[0]) && !visited_nodes.contains(edge_nodes[0])) {
							queue.add(edge_nodes[0]);
						}
					}
				}
			}
			current_node = null;
		}
		return connected_edges;
	}

	private static List<LinkedList<String>> distance(LinkedList<String> nodes, int trans) {
		for (int i = 0; i < nodes.size(); i++) {
			LinkedList<String> a = new LinkedList<String>();
			for (int j = 0; j < nodes.size(); j++) {
				if (i != j) {
					String[] tpoint = nodes.get(i).split(",");
					int x0 = Integer.parseInt(tpoint[0].replace("(", "").replace(" ", ""));
					int y0 = Integer.parseInt(tpoint[1].replace(")", "").replace(" ", ""));
					tpoint = nodes.get(j).split(",");
					int x1 = Integer.parseInt(tpoint[0].replace("(", "").replace(" ", ""));
					int y1 = Integer.parseInt(tpoint[1].replace(")", "").replace(" ", ""));
					float distance = (float) Math.sqrt(Math.pow(x1 - x0, 2) + Math.pow(y1 - y0, 2));
					if (distance <= trans) {
						a.add(nodes.get(j));
					}
				}
			}
			sensors.add(a);
		}
		return sensors;
	}

	public static void main(String args[]) {
		Scanner sc = new Scanner(System.in);
		while (true) {
			System.out.println("\nEnter the Network Length and Breadth: ");
			int l = sc.nextInt();
			int b = sc.nextInt();
			System.out.println("Enter the number of nodes: ");
			int n = sc.nextInt();
			System.out.println("Enter the Transmission Range: ");
			int t = sc.nextInt();
			System.out.println("Enter the number of data generators: ");
			int p = sc.nextInt();
			System.out.println("Enter the number of data packets: ");
			int q = sc.nextInt();
			System.out.println("Enter the storage capacity: ");
			int m = sc.nextInt();
			System.out.println("");
			for (int i = 0; i < n; i++) {
				int tbreadth = getRandom(b);
				int tlength = getRandom(l);
				String temp = "(" + tbreadth + ", " + tlength + ")";
				if (nodes.contains(temp)) {
					i--;
				} else {
					nodes.add("(" + tbreadth + ", " + tlength + ")");
				}
			}
			for (int i = 0; i < nodes.size(); i++) {
				System.out.println(i + "\t" + nodes.get(i) + "\n");
			}
			List<LinkedList<String>> sensors = distance(nodes, t);
			boolean graph = bfs(sensors, n, nodes);
			if (graph) {
				System.out.println("The Network is connected");
				if (p * q <= (n - p) * m) {
					LinkedList<String> __mst_path = kruskal();
					Visualizer vz = new Visualizer(l, b, nodes, sensors, __mst_path);
					vz.showGraph();
					break;
				}
			} else
				System.out.println("The Network is not connected");
			sc.nextLine();
			nodes.clear();
			sensors.clear();
			shotest.clear();
			cost = 0.0;
		}
		sc.close();
	}
}
