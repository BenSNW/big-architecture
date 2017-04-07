package hx.nlp.util;

import com.google.common.collect.ImmutableMap;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.StringUtils;
import hx.nlp.parser.temporal.TemporalExpression;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by zhipeng.wang on 03/23 2017.
 */
public class TemporalTokensUtil {

	private static Pattern datePattern = Pattern.compile("((\\d{2,4})年)?((\\d{1,2})月份?)?((\\d{1,2})日|号)?");
	private static Pattern timePattern = Pattern.compile("((凌晨|早上|早晨|清晨|上午|中午|下午|傍晚|黄昏|晚上|夜晚|半夜\\d{2,4})年)?((\\d{1,2})月份?)?((\\d{1,2})日|号)?");

	public static final String QIANTIAN = "前天";
	public static final String ZUOTIAN = "昨天";
	public static final String JINTIAN = "今天";
	public static final String MINGTIAN = "明天";
	public static final String CIRI = "次日";
	public static final String HOUTIAN = "后天";
	public static final String QIANNIAN = "前年";
	public static final String QUNIAN = "去年";
	public static final String JINNIAN = "今年";
	public static final String MINGNIAN = "明年";
	public static final String HOUNIAN = "后年";

	public static final String SHIYI = "十一";

	public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	private static final Map<String, Supplier<LocalDate>> ntDateMappers = ImmutableMap.<String, Supplier<LocalDate>>builder()
			.put(QIANTIAN, () -> LocalDate.now().minusDays(2))
			.put(ZUOTIAN, () -> LocalDate.now().minusDays(1))
			.put(JINTIAN, () -> LocalDate.now())
			.put(MINGTIAN, () -> LocalDate.now().plusDays(1))
			.put(CIRI, () -> LocalDate.now().plusDays(1))
			.put(HOUTIAN, () -> LocalDate.now().plusDays(2))

			.put(QIANNIAN, () -> LocalDate.of( thisYear() - 2, 1, 1))
			.put(QUNIAN, () -> LocalDate.of( thisYear() - 1, 1, 1)) // 阴历还是阳历
			.put(JINNIAN, () -> LocalDate.of( thisYear(), 1, 1))
			.put(MINGNIAN, () -> LocalDate.of( thisYear() + 1, 1, 1))
			.put(SHIYI, () -> LocalDate.of( thisYear(), 10, 1))
			.build();

	private static final Set<String> NT_WORDS = ntDateMappers.keySet();

	private static final Map<String, Supplier<TemporalExpression>> ntTemporals = ntDateMappers.entrySet()
			.stream().collect(Collectors.toMap(Map.Entry::getKey, TemporalTokensUtil::dateToTemporal));

	private static Supplier<TemporalExpression> dateToTemporal(Map.Entry<String, Supplier<LocalDate>> entry) {
		return () -> TemporalExpression.temporalDate(entry.getKey(), entry.getValue().get());
	}

//	private static final Map<String, Integer> dtMapper = ImmutableMap.<String, Integer>builder()
//			.put("前", -2).put("", -2).put("前", -2).put("前", -2)
//			.put("前", -2).put("前", -2).put("前", -2).put("前", -2)
//			.put("前", -2).put("前", -2).put("前", -2).put("前", -2)
//			.build();

	private static final Map<Character, Integer> cdMapper = ImmutableMap.<Character, Integer>builder()
//			.put('１', 1).put('２', 2).put('３', 3).put('４', 4).put('５', 5)
//			.put('６', 6).put('７', 7).put('８', 8).put('９', 9).put('０', 0)
			.put('零', 0).put('〇', 0).put('一', 1).put('二', 2).put('两', 2)
			.put('三', 3).put('四', 4).put('五', 5).put('六', 6).put('七', 7)
			.put('八', 8).put('九', 9).put('几', 3).put('多', 3).put('来', 2)
			.build();

	private static final Map<Character, Integer> cdUnitMapper = ImmutableMap.<Character, Integer>builder()
			.put('十', 10).put('百', 100).put('千', 1000).put('万', 10000).put('亿', 100000000)
			.build();

	public static int thisYear() {
		return LocalDate.now().getYear();
	}

	public static int thisMonth() {
		return LocalDate.now().getMonthValue();
	}

//	public static TemporalExpression parseNTDateToken(CoreLabel token) {
//		LocalDate.now().format()
//	}

	public static LocalDateTime parseNTTokens(List<CoreMap> ntTokens) {
		return parseNTTokens(LocalDateTime.now(), ntTokens);
	}

	public static LocalDateTime parseNTTokens(LocalDateTime reference, List<CoreMap> ntTokens) {
		int year = 1998, month = 12, day = 12;
		String text = ntTokens.stream().map(map -> map.get(CoreAnnotations.ValueAnnotation.class)).collect(Collectors.joining());
		Matcher matcher = datePattern.matcher(text);
		if (matcher.matches()) {
			month = Integer.valueOf(matcher.group(4));
		}
		LocalDateTime dateTime = reference;
		for (CoreMap token: ntTokens) {
			String nner = token.get(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class);
			if ( nner != null && !nner.equals("XXXX-XX-XX")) {

			}
		}

		return dateTime.withMonth(month);
	}

	public static List<Integer> parseNumber(String text) {
		try {
			return Arrays.asList(Integer.valueOf(text));
		} catch (NumberFormatException ex) {}

//		List<String> numbers = new ArrayList<>(Arrays.asList(text));
		List<String> numbers = new ArrayList<>(2);
		Pattern p = Pattern.compile("([一二两三四五六七八九]{2,3}|十几二十[几多来]?)");
		Matcher matcher = p.matcher(text);
		if (matcher.find()) {
			String match = matcher.group();
			int start = text.indexOf(match);
			String prefix = text.substring(0, start);
			String suffix = start + match.length() < text.length() ? text.substring(start + match.length()) : "";
			if (match.length() <= 3) {
				for (char c : match.toCharArray())
					numbers.add(prefix + c + suffix);
			} else {
				numbers.add(prefix + "十" + suffix);
				numbers.add(prefix + "三十" + suffix);
			}
		} else {
			numbers.add(text);
		}
		return numbers.stream().map(TemporalTokensUtil::parseSingleNumber).collect(Collectors.toList());
	}

	// 十万 百万 一百多万 三四百 一百七八 十几二十万 百十来天
	public static int parseSingleNumber(String text) {
		int cd = 0;
		int previousUnit = 1;
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (cdUnitMapper.containsKey(c)) {
				if (i == 0 || i == text.length() - 1)
					cd = Math.max(1, cd) * cdUnitMapper.get(c);
				else
					cd += 3 * cdUnitMapper.get(c);
				previousUnit = cdUnitMapper.get(c);
			} else {
				int n = c >= '0' && c <= '9' ? c - '0' : cdMapper.get(c);
				int currrentUnit = (i+1 < text.length() && cdUnitMapper.containsKey(text.charAt(i+1))) ?
					cdUnitMapper.get(text.charAt(i+1)) : 1;

				if (currrentUnit == 1) {
					cd += n * Math.max(1, previousUnit/10);
				} else if (currrentUnit > previousUnit) {
					i++;
					cd = (cd + n * Math.max(1, previousUnit/10)) * currrentUnit;
				} else {
					i++;
					cd += n * currrentUnit;
				}

				previousUnit = currrentUnit;
			}
		}
		return cd;
	}


	public static void main(String[] args) {
//		LocalDate date = LocalDate.parse("3月5日", DateTimeFormatter.ofPattern("'X'X'X'X-MM-DD"));
		System.out.println(datePattern.matcher("121"));

		System.out.println("3月5日".matches("((\\d{2,4})年)?((\\d{1,2})月份?)?((\\d{1,2})日|号)?"));

		Matcher matcher = datePattern.matcher("3月5日");
		int year = 1998, month = 12, day = 12;
		if (matcher.matches()) {
			System.out.println(matcher.group(2) + " " + matcher.group(4) + " " + matcher.group(6));
//			thisYear  = Integer.valueOf(matcher.group(2));
//			thisMonth = Integer.valueOf(matcher.group(4));
//			day   = Integer.valueOf(matcher.group(6));
		}

		System.out.println(LocalDateTime.now().minusDays(-2));
		System.out.println(year + " " + month + " " + day);

		Period period = LocalDate.now().until(LocalDate.now().plusDays(2));
		System.out.println(period);
		System.out.println(LocalDate.now().plus(period));

		Period.ofDays(12);

		Duration.ofSeconds(3601).getUnits().forEach(System.out::println);
//		Duration.of(2, ChronoUnit.YEARS);

		System.out.println(LocalDate.ofYearDay(2017, 1));
		System.out.println(LocalDate.of(2017, 3, 1).minusDays(1));

		Stream.of("三百零几", "十来万", "一百多万", "一百万", "三四十", "一百七八十万", "一百三四十多万", "十几二十多万")
				.forEach(text -> System.out.println(parseNumber(text)));

	}

}
