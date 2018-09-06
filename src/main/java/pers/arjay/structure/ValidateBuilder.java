package pers.arjay.structure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.winterframework.firefrog.game.entity.GameSlip;
import com.winterframework.firefrog.game.entity.MoneyMode;
import com.winterframework.firefrog.game.exception.GameBetAmountErrorException;

import pers.arjay.exception.SampleException;

/**
 * <pre>
 * 投注項驗證Builder Pattern。
 * 
 * betXXXX Method為第一層的處理
 * 
 * 若需細項處理用itemXXXX Method。操作如下：
 * 
 * 範例1：
 * 
 * 長度需大於三
 * String betDetail = "012345";
 * 
 * new BetDetailValidateBuilder(betDetail)
 * 		.betLengthGreat(3);
 * 
 * 此時bets 為 {"0","1","2","3","4","5"}
 * 
 * 
 * 
 * 範例2：
 * 
 * 投注項長度需大於三
 * 子項長度需大於二
 * 
 * String betDetail = "01,23,45,67";
 * 
 * new BetDetailValidateBuilder(betDetail,{@link Separator#comma})
 * 		.betLengthGreat(3)
 * 		.itemLengthGreat(2);
 * 
 * 此時 bets為：{"01","23","45","67"}
 * 
 * 範例3：
 * 
 * 子項不得重複，且有分隔符號
 * 
 * String betDetail = "0|1,2|3,4|5,6|7";
 * 
 * bets = new BetDetailValidateBuilder(betDetail,{@link Separator#comma},{@link Separator#vertical})
 * 			.itemNotRepeat();
 * 
 * 此時 bets為：{"0|1","2|3","4|5","6|7"}
 * 
 * </pre>
 * 
 * @author jay.kuo
 * @date 2017.10.02
 */
public class ValidateBuilder {

	private String betDetail;

	private String[] bets;

	private Separator betSeparator;

	private Separator itemSeparator;

	private Long betCounts;

	private Long totalAmount;

	private Integer multiple;

	private Integer unit = 1;

	private MoneyMode moneyMode;

	private int betCounter = 0;

	/**
	 * 此建構子為無號切割，且不需拆細單如：
	 * 
	 * String betDetail = "0123";
	 * 
	 * 會轉換成
	 * 
	 * String[] bets = {"0","1","2","3"};
	 * 
	 * @param gameSlip
	 *            投注單
	 */
	public ValidateBuilder(GameSlip gameSlip) {
		this(gameSlip, Separator.unsign, Separator.unsign, "");
	}

	/**
	 * 根據傳入的分隔separator切割，且不需拆細單如：
	 * 
	 * <pre>
	 * String regex = {@link Separator#multiple};
	 * 
	 * String betDetail = "01,02,03,04";
	 * 
	 * bets = {"01","02","03","04"};
	 * </pre>
	 * 
	 * @param gameSlip
	 *            投注單
	 * @param betSeparator
	 *            split的regex參數
	 */
	public ValidateBuilder(GameSlip gameSlip, Separator betSeparator) {
		this(gameSlip, betSeparator, Separator.unsign, "");
	}

	/**
	 * 根據傳入的分隔separator切割，取代傳入參數後無號拆單，類似於：
	 * 
	 * <pre>
	 * String regex = {@link Separator#multiple};
	 * 
	 * String betDetail = "01,-,-,02,03,04".replaceAll(replace,"");
	 * 
	 * bets = {"01","02","03","04"};
	 * </pre>
	 * 
	 * @param gameSlip
	 *            投注單
	 * @param betSeparator
	 *            split的regex參數
	 * @param replace
	 *            預設取代的文字
	 */
	public ValidateBuilder(GameSlip gameSlip, Separator betSeparator, String replace) {
		this(gameSlip, betSeparator, Separator.unsign, replace);
	}

	/**
	 * 根據傳入的分隔separator切割，如：
	 * 
	 * <pre>
	 * String betSeparator = {@link Separator#comma};
	 * 
	 * String itemSeparator = {@link Separator#space};
	 * 
	 * String betDetail = "01 03,08 09,02 07,03 05,04 08";
	 * 
	 * bets = {"01 03","08 09","02 07","03 05","04 08"};
	 * 
	 * items = {{"01","03"},{"08","09"},{"02","07"},{"03","05"},{"04","08"}}
	 * 
	 * </pre>
	 * 
	 * @param gameSlip
	 *            投注單
	 * @param betSeparator
	 *            split的regex參數
	 * @param itemSeparator
	 *            拆單的regex參數
	 */
	public ValidateBuilder(GameSlip gameSlip, Separator betSeparator, Separator itemSeparator) {
		this(gameSlip, betSeparator, itemSeparator, "");
	}

	/**
	 * 取代replace參數後再根據傳入的分隔separator切割，如：
	 * 
	 * <pre>
	 * String betSeparator = {@link Separator#comma};
	 * 
	 * String itemSeparator = {@link Separator#space};
	 * 
	 * String betDetail = "01 03,08 09,-,-,-,02 07,03 05,04 08".replaceAll(replace,"");
	 * 
	 * <B>注意，這邊空白可能會有問題</B>
	 * bets = {"01 03","08 09",,,,"02 07","03 05","04 08"};
	 * 
	 * items = {{"01","03"},{"08","09"},{"02","07"},{"03","05"},{"04","08"}}
	 * 
	 * </pre>
	 * 
	 * @param gameSlip
	 *            投注單
	 * @param betSeparator
	 *            split的regex參數
	 * @param itemSeparator
	 *            拆單的regex參數
	 */
	public ValidateBuilder(GameSlip gameSlip, Separator betSeparator, Separator itemSeparator, String replace) {
		this.betCounts = gameSlip.getTotalBet();
		this.betDetail = gameSlip.getBetDetail();
		this.totalAmount = gameSlip.getTotalAmount();
		this.multiple = gameSlip.getMultiple();
		this.moneyMode = gameSlip.getMoneyMode();

		this.betSeparator = betSeparator;
		this.itemSeparator = itemSeparator;

		this.bets = betDetail.split(betSeparator.regex());

		/* 如果一開始先取代資料後，切割出來的投注項會有問題 */
		for (int i = 0; i < bets.length; i++) {
			bets[i] = bets[i].replaceAll(replace, "");
		}

	}

	/**
	 * 投注內容存在於傳入集合內
	 * 
	 * @param values
	 *            驗證集合
	 */
	public ValidateBuilder betIn(final Collection<String> values) {
		for (String bet : bets) {
			if (!values.contains(bet)) {
				throw new SampleException("投注内容有誤，錯誤項目:{}", bet);
			}
		}
		return this;
	}

	/**
	 * 投注號碼於區間內
	 * 
	 * @param min
	 *            最小值
	 * @param max
	 *            最大值
	 */
	public ValidateBuilder betNumberBetween(final int min, final int max) {
		for (String bet : bets) {
			try {
				Integer betNumber = Integer.parseInt(bet);
				if (betNumber < min || betNumber > max) {
					throw new SampleException("投注號碼數值需落於{}～{}之間,投注内容為：{}", min, max, bet);
				}
			} catch (NumberFormatException e) {
				throw new SampleException("投注號碼不為數值：{}, separator:{}", bet, betSeparator);
			}
		}
		return this;
	}

	/** 投注內容須為數值 */
	public ValidateBuilder betIsNumber() {
		for (String bet : bets) {
			if (!bet.matches("[0-9]+")) {
				throw new SampleException("內含不為數值之文字：{}, separator:{}", betDetail, betSeparator);
			}
		}
		return this;
	}

	/**
	 * 透過分割運算後，投注內容需介於於驗證長度
	 * 
	 * @param min
	 *            最小長度
	 * @param max
	 *            最大長度
	 */
	public ValidateBuilder betLengthBetween(int min, int max) {
		if (bets.length < min || bets.length > max) {
			throw new SampleException("長度需落於{}～{}之間,投注内容長度為：{}", min, max, bets.length);
		}
		return this;
	}

	/**
	 * 投注內容需為驗證長度
	 * 
	 * @param length
	 *            驗證長度
	 */
	public ValidateBuilder betLengthEquals(int length) {
		if (bets.length != length) {
			throw new SampleException("投注内容長度不為：{} bets:{}", length, bets.length);
		}
		return this;
	}

	/**
	 * 投注內容需大於驗證長度
	 * 
	 * @param length
	 *            驗證長度
	 */
	public ValidateBuilder betLengthGreatEqualsThen(int length) {
		if (bets.length < length) {
			throw new SampleException("長度需大於{},投注内容長度為：{}", length, bets.length);
		}
		return this;
	}

	/**
	 * 投注內容需小於驗證長度
	 * 
	 * @param length
	 *            驗證長度
	 */
	public ValidateBuilder betLengthLessEqualsThen(int length) {
		if (bets.length > length) {
			throw new SampleException("長度需小於{},投注内容長度為：{}", length, bets.length);
		}
		return this;
	}

	/** 投注內容不得為空 */
	public ValidateBuilder betNotBlank() {
		if (StringUtils.isBlank(betDetail)) {
			throw new SampleException("投注内容為空");
		}

		return this;
	}

	/** 投注內容不得重複 */
	public ValidateBuilder betNotRepeat() {
		if (new HashSet<>(Arrays.asList(bets)).size() != bets.length) {
			throw new SampleException("投注内容有值重複,{}", betDetail);
		}
		return this;
	}

	/** 透過分割運算後，細項須為數值 */
	public ValidateBuilder itemIsNumber() {
		for (String bet : bets) {
			for (String item : bet.split(itemSeparator.regex())) {
				if (StringUtils.isBlank(item)) {
					continue;
				}
				if (!item.matches("[0-9]+")) {
					throw new SampleException("投注細項須為數值:{}", item);
				}
			}
		}
		return this;
	}

	/**
	 * 細項號碼於區間內
	 * 
	 * @param min
	 *            最小值
	 * @param max
	 *            最大值
	 */
	public ValidateBuilder itemNumberBetween(final int min, final int max) {
		for (String bet : bets) {
			for (String item : bet.split(itemSeparator.regex())) {
				if (StringUtils.isBlank(item)) {
					continue;
				}

				try {
					Integer betNumber = Integer.parseInt(item);
					if (betNumber < min || betNumber > max) {
						throw new SampleException("投注細項數值需落於{}～{}之間,細項内容為：{}", min, max, item);
					}
				} catch (NumberFormatException e) {
					throw new SampleException("投注細項不為數值：{}, betSeparator:{}, itemSeparator:{}",
							item, betSeparator, itemSeparator);
				}
			}
		}
		return this;
	}

	/**
	 * 透過分割運算後，細項內容需介於於驗證長度
	 * 
	 * @param min
	 *            最小長度
	 * @param max
	 *            最大長度
	 */
	public ValidateBuilder itemLengthBetween(int min, int max) {
		for (String item : bets) {
			Integer itemLength = item.split(itemSeparator.regex()).length;
			if (itemLength < min || itemLength > max) {
				throw new SampleException("細項長度需落於{}～{}之間,細項長度為：{}", min, max, item.length());
			}
		}
		return this;
	}

	/**
	 * 透過分割運算後，細項內容需等於驗證長度
	 * 
	 * @param length
	 *            驗證長度
	 */
	public ValidateBuilder itemLengthEquals(int length) {
		for (String item : bets) {
			if (item.split(itemSeparator.regex()).length != length) {
				throw new SampleException("細項投注内容長度不為：{} , {}", length, item);
			}
		}
		return this;
	}

	/**
	 * 細項內容需大於驗證長度
	 * 
	 * @param length
	 *            驗證長度
	 */
	public ValidateBuilder itemLengthGreatEqualsThen(int length) {
		for (String item : bets) {
			if (item.split(itemSeparator.regex()).length < length) {
				throw new SampleException("細項長度需大於{},細項長度為：{}", length, item.length());
			}
		}
		return this;
	}

	/**
	 * 透過分割運算後，細項內容需小於驗證長度
	 * 
	 * @param length
	 *            驗證長度
	 */
	public ValidateBuilder itemLengthLessEqualsThen(int length) {
		for (String item : bets) {
			if (item.split(itemSeparator.regex()).length > length) {
				throw new SampleException("細項長度需小於{},細項長度為：{}", length, item.length());
			}
		}
		return this;
	}

	/** 細項內容不得為空值 */
	public ValidateBuilder itemNotBlank() {
		for (String item : bets) {
			if (StringUtils.isBlank(item)) {
				throw new SampleException("投注細項為空");
			}
		}
		return this;
	}

	/**
	 * 透過分割運算後，細項不得重複
	 */
	public ValidateBuilder itemNotRepeat() {
		for (String item : bets) {
			if (new HashSet<>(Arrays.asList(item.split(itemSeparator.regex())))
					.size() != item.split(itemSeparator.regex()).length) {
				throw new SampleException("細項有值重複, {}", item);
			}
		}

		return this;
	}

	/** 投注注數等於計算注數 */
	public ValidateBuilder betCountsEqualsBets() {
		this.checkBetCounterNotProcessed();

		betCounter = bets.length;

		if (betCounts != betCounter) {
			throw new SampleException("[betCountsEqualsBets] 投注注數不符：{}:{}", betCounts, bets.length);
		}
		return this;
	}

	/**
	 * 投注注數等於排列組合數(Cn取m)
	 * 
	 * @param betLength
	 *            需投注長度 (m值)
	 */
	public ValidateBuilder betCountsEqualsCombinBets(Integer betLength) {
		this.checkBetCounterNotProcessed();

		betCounter = CombinUtil.combin(bets.length, betLength);
		if (betCounter != betCounts) {
			throw new SampleException("[betCountsEqualsCombinBets] 投注注數不符：傳入注數{}, 計算注數：{}",
					betCounts, betCounter);
		}

		return this;
	}

	/**
	 * <pre>
	 * 投注注數等於排列組合後注數 (不重複)如：
	 * 
	 * 12,12 => 1 2, 2 1 共兩注。
	 * 
	 * 123,123,123 共六注
	 * 
	 * 123,234,345 共14注
	 * 
	 * 參考驗證程式範例：
	 * 
	 * GameSlip gameSlip = new GameSlip(); 
	 * gameSlip.setBetDetail("123,234,345");
	 * gameSlip.setTotalBet(6L);
	 * new ValidateBuilder(gameSlip, Separator.comma,Separator.unsign).betCountsEqualsPermutationItems();
	 * </pre>
	 */
	public ValidateBuilder betCountsEqualsPermutationItems() {
		this.checkBetCounterNotProcessed();

		List<BetTree> tree = this.generatorBetTree();
		
		for (BetTree root : tree) {
			betCounter += root.count(bets.length);
		}

		if (betCounter != betCounts) {
			throw new SampleException("[betCountsEqualsPermutationItems] 投注注數不符：傳入注數{}, 計算注數：{}",
					betCounts, betCounter);
		}

		return this;
	}

	/**
	 * 透過分割運算後，投注注數等於細項注數
	 */
	public ValidateBuilder betCountsEqualsSumItems() {
		this.checkBetCounterNotProcessed();

		for (String bet : bets) {
			if (!StringUtils.isBlank(bet)) {
				betCounter += bet.split(itemSeparator.regex()).length;
			}
		}

		if (this.betCounts != betCounter) {
			throw new SampleException("[betCountsEqualsSumItems] 投注注數不符：{}:{}", betCounts,
					betCounter);
		}
		return this;
	}

	/**
	 * 需先執行betCounts* 相關驗證。
	 */
	public ValidateBuilder totalAmountValid() {
		this.checkBetCounterIsProcessed();

		if (totalAmount != (betCounter * multiple * unit * (moneyMode == MoneyMode.YUAN ? 10000L : 1000L))) {
			throw new GameBetAmountErrorException("[AmountEqualsBetCalculate]投注金额不对:{}:{}", totalAmount,
					betCounter * multiple * unit * (moneyMode == MoneyMode.YUAN ? 10000L : 1000L));
		}
		return this;
	}

	/**
	 * 設定單位，預設為1，如有特別需求如：快三系列，單位為2，則需一開始先設定驗證單位。
	 * 
	 * @param unit
	 *            每注單位價格
	 * 
	 */
	public ValidateBuilder setUnit(Integer unit) {
		this.unit = unit;
		return this;
	}

	private void checkBetCounterIsProcessed() {
		if (betCounter == 0) {
			throw new SampleException("請先執行betCounts* 相關驗證。");
		}
	}

	private void checkBetCounterNotProcessed() {
		if (betCounter != 0) {
			throw new SampleException("已執行betCounts* 相關驗證，請確認流程。");
		}
	}

	/**
	 * 產生投注的排列組合樹
	 */
	private List<BetTree> generatorBetTree() {
		List<BetTree> tree = new ArrayList<>();

		for (int level = 0; level < bets.length; level++) {
			if (CollectionUtils.isEmpty(tree)) {
				for (String root : bets[level].split(itemSeparator.regex())) {
					tree.add(new BetTree(root));
				}
			} else {
				for (BetTree root : tree) {
					for (String item : bets[level].split(itemSeparator.regex())) {
						root.addNode(level, item);
					}
				}
			}
		}

		return tree;
	}

	public ValidateBuilder isSingleNote() {
		if (betDetail.split(itemSeparator.comma.toString()).length > 1) {
			throw new SampleException("投注项目不得为复式。");
		}
		return this;
	}
	
	public String[] getBets() {
		return bets;
	}
	
}
