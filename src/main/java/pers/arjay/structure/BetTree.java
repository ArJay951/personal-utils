package pers.arjay.structure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.springframework.util.CollectionUtils;

import lombok.ToString;

@ToString
public class BetTree {

	private static final int root = 0;

	private String value;

	private Integer level;

	private Collection<BetTree> childs;

	public BetTree(String value) {
		this.level = root;
		this.value = value;
		this.childs = new HashSet<>();
	}

	public BetTree(Integer level, String value) {
		this.level = level;
		this.value = value;
		this.childs = new HashSet<>();
	}

	public String getValue() {
		return value;
	}

	public Integer count(int betLength) {
		int sum = 0;
		for (BetTree child : childs) {
			sum += child.count(betLength);
		}
		return sum == 0 && this.level == (betLength - 2) ? childs.size() : sum;
	}

	public Collection<String> generateBets(Collection<String> bets, String parent, String separator) {
		if (bets == null) {
			bets = new ArrayList<>();
		}

		if (CollectionUtils.isEmpty(childs)) {
			bets.add(parent + separator + value);
		} else {
			for (BetTree child : childs) {
				bets = child.generateBets(bets, parent + separator + value, separator);
			}
		}

		return bets;
	}

	public void addNode(Integer level, String value) {
		this.addNode(level, value, false);
	}

	public void addNode(Integer level, String value, boolean repeatable) {
		if (!repeatable && this.value.equals(value)) {
			return;
		}

		if (this.level == level - 1) {
			childs.add(new BetTree(level, value));
		} else {
			for (BetTree child : childs) {
				child.addNode(level, value, repeatable);
			}
		}
	}

}
