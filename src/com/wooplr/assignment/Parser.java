package com.wooplr.assignment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

class Result {

	Tuple tuple;
	String relation;

	public Result(String name1, String name2, String relation) {

		this.tuple = new Tuple(name1, name2);
		this.relation = relation;
	}

	public Result(Tuple tuple, String relation) {
		this.tuple = tuple;
		this.relation = relation;
	}

	public String toString() {

		return relation + " " + tuple.toString();
	}
}

public class Parser {

	CreateKnowledgeBase knowledgeBase;

	public Parser() {
		knowledgeBase = new CreateKnowledgeBase();
		knowledgeBase.fromFile();
	}

	List<Result> query(String[] query) {

		query[1] = query[1].substring(1, query[1].length() - 1).trim();

		List<Result> list = new LinkedList<>();

		if (query[0].charAt(0) == '?' && query[2].charAt(0) == '?' && !query[0].equals(query[2])) {

			List<Tuple> queryResults = knowledgeBase.DBase.get(query[1]);
			Iterator<Tuple> itr = queryResults.iterator();
			while (itr.hasNext()) {
				list.add(new Result(itr.next(), query[1]));
			}
			return list;
		}

		else if (query[0].charAt(0) == '<') {

			List<Tuple> queryResults = knowledgeBase.DBase.get(query[1]);
			Iterator<Tuple> itr = queryResults.iterator();
			while (itr.hasNext()) {
				Tuple tuple = itr.next();
				String name = query[0].substring(1, query[0].length() - 1).trim();
				if (tuple.E1.name.equals(name))
					list.add(new Result(new Tuple(new Entity(tuple.E1.name, tuple.E1.type + "Constant"), new Entity(
							tuple.E2.name, tuple.E2.type.substring(0, tuple.E2.type.length() - 1))), query[1]));
			}
			return list;
		}

		else if (query[2].charAt(0) == '<') {
			List<Tuple> queryResults = knowledgeBase.DBase.get(query[1]);
			Iterator<Tuple> itr = queryResults.iterator();
			while (itr.hasNext()) {
				Tuple tuple = itr.next();
				String name = query[2].substring(1, query[2].length() - 1).trim();
				if (tuple.E2.name.equals(name))
					list.add(new Result(new Tuple(new Entity(tuple.E1.name, tuple.E1.type.substring(0,
							tuple.E1.type.length() - 1)), new Entity(tuple.E2.name, tuple.E2.type + "Constant")),
							query[1]));
			}
			return list;
		}

		else
			return null;
	}

	void printEverything() {

		Map<String, List<Tuple>> map = knowledgeBase.DBase;

		System.out.println("A, B, C");
		for (Entry<String, List<Tuple>> e : map.entrySet()) {

			String relation = e.getKey();
			List<Tuple> tupleList = e.getValue();
			Iterator<Tuple> itr = tupleList.iterator();
			while (itr.hasNext()) {
				Tuple tuple = (Tuple) itr.next();
				System.out.println(tuple.E1.name + "," + relation + tuple.E2.name);
			}
		}
	}

	void printResults(List<List<Entity>> results, String qParts) {

		// Let's select the fields that were enquired

		String[] fields = qParts.trim().split(" ");

		for (int i = 1; i < fields.length; i++) {

			fields[i] = fields[i].trim();
			fields[i] = fields[i].substring(1);
		}

		int i = 1;
		for (i = 1; i < fields.length - 1; i++)
			System.out.print(fields[i] + ",");
		System.out.println(fields[i]);

		Iterator<List<Entity>> iter = results.iterator();

		while (iter.hasNext()) {

			List<Entity> list = iter.next();

			for (int k = 1; k < fields.length; k++) {

				Iterator<Entity> iter1 = list.iterator();
				boolean done = false;
				while (iter1.hasNext()) {

					Entity e = iter1.next();
					if (k < fields.length - 1) {
						if (e.type.equalsIgnoreCase(fields[k])) {
							System.out.print(e.name + ",");
							done = true;
						}
					} else if (k == fields.length - 1) {
						if (e.type.equalsIgnoreCase(fields[k])) {
							System.out.println(e.name);
							done = true;
						}
					}

				}

				if (!done) {

					iter1 = list.iterator();
					while (iter1.hasNext()) {
						Entity e = iter1.next();
						if (k < fields.length - 1) {
							if (e.type.equalsIgnoreCase(fields[k].substring(0, fields[k].length() - 1))) {
								System.out.print(e.name + ",");
							}
						} else if (k == fields.length - 1) {
							if (e.type.equalsIgnoreCase(fields[k].substring(0, fields[k].length() - 1))) {
								System.out.println(e.name);
							}
						}
					}
				}
			}
		}
	}

	void handleResults(String[] qParts, String[][] allQueries, List<List<Result>> allResults) {

		// Just to clarify, qParts has the query string separated over the word
		// "where".
		// allQueries has the second part of qParts separated over a dot (".").
		// allResults holds the results of all individual queries.

		// Now that we have the results of all individual queries, let try to
		// club the results
		int numQueries = allQueries.length;

		// Create a structure to hold our compound results.
		List<List<Entity>> finalResult = new LinkedList<List<Entity>>();

		// Mix up the results to form compounded outputs.
		for (int k = 0; k < numQueries - 1; k++) {

			List<Result> list1 = allResults.get(k);
			List<Result> list2 = allResults.get(k + 1);
			Iterator<Result> iter1 = list1.iterator();
			while (iter1.hasNext()) {

				Result result1 = iter1.next();
				Iterator<Result> iter2 = list2.iterator();
				while (iter2.hasNext()) {

					Result result2 = iter2.next();
					if (allQueries[k][0].equals(allQueries[k + 1][0])) {

						if (result1.tuple.E1.equals(result2.tuple.E1)) {

							List<Entity> answer = new LinkedList<>();
							answer.add(result1.tuple.E1);
							answer.add(result1.tuple.E2);
							answer.add(result2.tuple.E2);

							finalResult.add(answer);
						}

					} else if (allQueries[k][0].equals(allQueries[k + 1][2])) {

						if (result1.tuple.E1.equals(result2.tuple.E2)) {

							List<Entity> answer = new LinkedList<>();
							answer.add(result2.tuple.E1);
							answer.add(result1.tuple.E1);
							answer.add(result1.tuple.E2);
							finalResult.add(answer);
						}

					} else if (allQueries[k][2].equals(allQueries[k + 1][2])) {

						if (result1.tuple.E2.equals(result2.tuple.E2)) {

							List<Entity> answer = new LinkedList<>();
							answer.add(result1.tuple.E1);
							answer.add(result1.tuple.E2);
							answer.add(result2.tuple.E1);

							finalResult.add(answer);
						}

					} else if (allQueries[k][2].equals(allQueries[k + 1][0])) {

						if (result1.tuple.E2.equals(result2.tuple.E1)) {

							List<Entity> answer = new LinkedList<>();
							answer.add(result1.tuple.E1);
							answer.add(result1.tuple.E2);
							answer.add(result2.tuple.E2);

							finalResult.add(answer);
						}
					}
				}
			}
		}

		if (numQueries == 1) {
			Iterator<List<Result>> iter1 = allResults.iterator();
			while (iter1.hasNext()) {

				List<Result> temp = iter1.next();
				Iterator<Result> iter2 = temp.iterator();
				while (iter2.hasNext()) {

					Result result = iter2.next();
					List<Entity> answer = new LinkedList<>();
					answer.add(result.tuple.E1);
					answer.add(result.tuple.E2);

					finalResult.add(answer);
				}
			}
		}

		printResults(finalResult, qParts[0]);
	}

	public boolean parse(String query) {

		/**
		 * This method will parse the input query and returns the result in an
		 * CSV file with first row as variable names and next rows as binding of
		 * these variables.
		 */

		String[] qParts = query.split("where");

		qParts[1] = qParts[1].trim(); // Remove any leading or trailing
										// whitespaces.

		if (qParts[1].equals("{}")) {
			System.out.println("Parse Error");
			return false;

		} else if (qParts[0].split(" ")[1].equals("*") && (qParts[1].replaceAll(" ", "").split("\\?").length == 4)) {

			printEverything();
			return true;
		} else if (qParts[0].split(" ")[1].equals("*")) {

			System.out.println("Parse Error");
			return false;
		}

		int i = 1, j = qParts[1].length() - 1; // Done for removing the curly
												// braces as well as space next
												// to these braces.
		while (qParts[1].charAt(i) == ' ') {
			i++;
		}
		while (qParts[1].charAt(j) == ' ') {
			j--;
		}
		qParts[1] = qParts[1].substring(i, j - 1);
		String[] subQueries = qParts[1].split("\\."); // separate out multiple
														// queries by splitting
														// around "."

		// A 2D array to store all individual queries.
		String[][] allQueries = new String[subQueries.length][3];

		for (int k = 0; k < subQueries.length; k++) {

			// The split around space assumes that there are no white spaces
			// once the angular brackets start.
			allQueries[k] = subQueries[k].trim().split(" ");
			if (allQueries[k].length < 3) {
				System.out.println("Parse Error");
				return false;
			}

		}

		// Create a structure to contain the results of all subQueries.
		List<List<Result>> allResults = new ArrayList<List<Result>>();

		for (int k = 0; k < subQueries.length; k++)
			allResults.add(query(allQueries[k]));

		handleResults(qParts, allQueries, allResults);

		return true;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// First test case
		Parser parser = new Parser();
		// parser.parse("Select ?person1 where {}");
		// parser.parse("Select ?person1 where {<Shelden> <hasFriend>}");
		 parser.parse("Select * where {? a  ?b   ?c.}");
		// parser.parse("Select ?person where {<Shelden> <hasFriend> ?person");
		// parser.parse("select ?person1 ?person2 where { ?person1 <hasFriend> ?person2. } ");
		// parser.parse("select ?person ?University where { <Shelden> <hasFriend> ?person . ?person <worksAt> ?university } ");
		// parser.parse("Select ?person1 ?person2 where { ?person1 <worksAt> <Caltech> . ?person2 <bornIn> <Nabraska> . ?person1 <hasFriend> ?person2 .}");

		/**
		 * The result will be stored as a CSV file result<test case number>.csv.
		 * for example, first test case result will be stored as result1.csv.
		 */

	}

}