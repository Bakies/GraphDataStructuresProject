package es.baki.dsp7;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

public class Graph {
	private Vertex entrance, exit;
	private ArrayList<Vertex> vertices;

	public Graph() {
		vertices = new ArrayList<Vertex>();
	}

	public void addVertex(int x, int y) {
		if (!vertices.contains(getVertex(x, y))){
			vertices.add(getVertex(x, y));
		}
	}

	private Vertex getVertex(int x, int y) {
		for (Vertex v : vertices){
			if (v.x == x && v.y == y){
				return v;
			}
		}
		return new Vertex(x, y);
	}

	public void addEntrance(int x, int y) {
		Vertex v = getVertex(x, y);
		addVertex(x, y);
		entrance = v;
	}

	public void addExit(int x, int y) {
		Vertex v = getVertex(x, y);
		addVertex(x, y);
		exit = v;
	}

	public void addConnection(int x1, int y1, int x2, int y2) {
		Vertex v1 = getVertex(x1, y1), v2 = getVertex(x2, y2);

		addVertex(x1, y1);
		addVertex(x2, y2);
		
		v1.addConnection(v2);
		v2.addConnection(v1);
	}

	public void readInFromFile(String filename) {
		File file = new File(filename);
		Scanner scan;
		try {
			scan = new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}

		/**
		 * modes:
		 * 0 - none/initial
		 * 1 - reading in points
		 * 2 - reading in entrances
		 * 3 - reading in exits
		 * 4 - reading in connections
		 */
		int mode = 0;
		while (scan.hasNext()) {
			String input = scan.nextLine();
			String[] splits = input.split(" ");
			if (input.equalsIgnoreCase("POINTS")) {
				mode = 1;
				continue;
			} else if (input.equalsIgnoreCase("ENTRANCES")) {
				mode = 2;
				continue;
			} else if (input.equalsIgnoreCase("EXITS")) {
				mode = 3;
				continue;
			} else if (input.equalsIgnoreCase("CONNECTIONS")) {
				mode = 4;
				continue;
			} 
			int x = Integer.parseInt(splits[0]);
			int y = Integer.parseInt(splits[1]);
			switch(mode){
			case 1:
				this.addVertex(x, y);
				break;
			case 2:
				this.addEntrance(x, y);
				break;
			case 3:
				this.addExit(x, y);
				break;
			case 4:
				int x2 = Integer.parseInt(splits[2]), y2 = Integer.parseInt(splits[3]);
				this.addConnection(x, y, x2, y2);
				break;
			default:
				System.err.println("Error while reading in, no sections headers?");
			}

		}
		scan.close();
	}

	private class Vertex {
		private int x, y;
		private ArrayList<Vertex> connections;
		private boolean discovered = false;

		private boolean discovered() {
			return discovered;
		}
		private void discover() {
			discovered = true;
		}
		private void undiscover() {
			discovered = false;
		}

		private Vertex(int x, int y) { 
			connections = new ArrayList<Vertex>();
			this.x = x;
			this.y = y;
		}

		private void addConnection(Vertex v) {
			if (v == null)
				return;
			this.connections.add(v);
			
		}		
	}
	
	/*
	 * 1  procedure DFS-iterative(G,v):
2      let S be a stack
3      S.push(v)
4      while S is not empty
5          v = S.pop()
6          if v is not labeled as discovered:
7              label v as discovered
8              for all edges from v to w in G.adjacentEdges(v) do
9                  S.push(w)
	 */
	public void depthFirstSearch() {
		for (Vertex v : vertices) {
			v.undiscover();
		}
		Vertex v;
		ArrayList<Vertex> s = new ArrayList<>();
		s.add(entrance);
		while(s.size() != 0) {
			String out = "";
			for (int x = 0; x < s.size(); x ++){
				out += s.get(x);
			}
			System.out.println(out);
			v = s.remove(0);
			if (!v.discovered ){
				v.discover();
				for (Vertex w : v.connections) {
					s.add(0, w);
				}
			}
		}
	}
	
	public String toString() {
		char[][] map = new char[maxX() + 1][maxY() + 1]; 

		for (int y = 0 ; y <= maxY(); y ++) {
			for (int x = 0; x <= maxX(); x++) {
				map[x][y] = ' ';
			}
		}		
		for (Vertex v : vertices) {
			map[v.x][v.y] = 'O';
		}
		String s = "";
		
		for (Vertex v : vertices) {
			for (Vertex target : v.connections) {
				if (v.x != target.x)
					for (int x = Math.min(v.x, target.x) + 1; x < Math.max(v.x, target.x); x ++) {
						map[x][v.y] = '-';
					}
				else 
					for (int y = Math.min(v.y, target.y) + 1; y < Math.max(v.y, target.y); y ++) {
						map[v.x][y] = '|';
					}
			}
		}
			
		
		for (int y = maxY(); y >= 0; y --) {
			s += String.format("%3d", y);
			for (int x = 0; x <= maxX(); x++) {
				s += map[x][y];  
			}
			s += String.format("%n");
		}
		return s;
	}
	
	private int maxY() {
		int max = 0;
		for (Vertex v : vertices) {
			max = Math.max(max, v.y);
		}
		return max;
	}

	private int maxX() {
		int max = 0;
		for (Vertex v : vertices) {
			max = Math.max(max, v.x);
		}
		return max;
	}

	public static void main(String...strings) {
		Graph g = new Graph();
		g.readInFromFile("GraphFileExample");
		
		System.out.println(g);
		g.depthFirstSearch();
	}
	

}
