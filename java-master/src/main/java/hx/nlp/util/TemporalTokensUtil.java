package hx.nlp.util;

import com.google.common.collect.ImmutableMap;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import hx.nlp.parser.temporal.TemporalExpression;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
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

	public static Pattern datePattern = Pattern.compile("((\\d{2,4})年)?((\\d{1,2})月份?)?((\\d{1,2})日|号)?");
	public static Pattern timePattern = Pattern.compile("((凌晨|早上|早晨|清晨|上午|中午|下午|傍晚|黄昏|晚上|夜晚|半夜\\d{2,4})年)?((\\d{1,2})月份?)?((\\d{1,2})日|号)?");

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

	public static final Map<String, Supplier<LocalDate>> ntDateMappers = ImmutableMap.<String, Supplier<LocalDate>>builder()
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

	public static final Set<String> NT_WORDS = ntDateMappers.keySet();

	public static final Map<String, Supplier<TemporalExpression>> ntTemporals = ntDateMappers.entrySet()
			.stream().collect(Collectors.toMap(Map.Entry::getKey, TemporalTokensUtil::dateToTemporal));

	public static Supplier<TemporalExpression> dateToTemporal(Map.Entry<String, Supplier<LocalDate>> entry) {
		return () -> TemporalExpression.temporalPoint(entry.getKey(), entry.getValue().get());
	}

	public static final Map<Character, Integer> cdMapper = ImmutableMap.<Character, Integer>builder()
			.put('零', 0).put('〇', 0).put('一', 1).put('二', 2).put('两', 2)
			.put('三', 3).put('四', 4).put('五', 5).put('六', 6).put('七', 7)
			.put('八', 8).put('九', 9).put('几', 3).put('多', 3).put('来', 2)
			.put('1', 1).put('2', 2).put('3', 3).put('4', 4).put('5', 5)
			.put('6', 6).put('7', 7).put('8', 8).put('9', 9).put('0', 0)
			.put('１', 1).put('２', 2).put('３', 3).put('４', 4).put('５', 5)
			.put('６', 6).put('７', 7).put('８', 8).put('９', 9).put('０', 0)
			.build();

	public static final Map<Character, Integer> cdUnitMapper = ImmutableMap.<Character, Integer>builder()
			.put('十', 10).put('百', 100).put('千', 1000).put('万', 10000).put('亿', 100000000)
			.build();

	public static final Map<String, Long> ntUnitMapper = ImmutableMap.<String, Long>builder()
			.put("秒", 1L).put("秒钟", 1L).put("分", 60L).put("分钟", 60L)
			.put("刻", 900L).put("刻钟", 900L).put("小时", 3600L).put("钟头", 3600L)
			.put("天", 24 * 3600L).put("日", 24 * 3600L)
			.put("周", ChronoUnit.WEEKS.getDuration().getSeconds())
			.put("星期", ChronoUnit.WEEKS.getDuration().getSeconds())
			.put("礼拜", ChronoUnit.WEEKS.getDuration().getSeconds())
			.put("月", ChronoUnit.MONTHS.getDuration().getSeconds())
			.put("季", 4 * ChronoUnit.MONTHS.getDuration().getSeconds())
			.put("季度", 4 * ChronoUnit.MONTHS.getDuration().getSeconds())
			.put("年", ChronoUnit.YEARS.getDuration().getSeconds())
			.put("年度", ChronoUnit.YEARS.getDuration().getSeconds())
			.put("世纪", ChronoUnit.CENTURIES.getDuration().getSeconds())
			.build();

	public static final Map<String, Integer> dtMapper = ImmutableMap.<String, Integer>builder()
		.put("前", -1).put("后", 1).put("上", -1).put("下", -2).put("这", -1)
		.put("最近", -1)
		.build();

	public static int thisYear() {
		return Year.now().getValue();
	}

	public static int thisMonth() {
		return LocalDate.now().getMonthValue();
	}

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
			} else if (cdMapper.containsKey(c)){
//				int n = c >= '0' && c <= '9' ? c - '0' : cdMapper.get(c);
				int n = cdMapper.get(c);
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

	public static ChronoUnit getChronoUnit(String unit) {
		return secondsToChronoUnit(ntUnitMapper.get(unit));
	}

	public static ChronoUnit secondsToChronoUnit(long seconds) {
		return Stream.of(ChronoUnit.values()).sorted(Comparator.comparing(ChronoUnit::getDuration).reversed())
			.filter(cu -> seconds >= cu.getDuration().getSeconds())
			.findFirst().orElse(ChronoUnit.MINUTES);    // 刻钟
//		ChronoUnit[] units = ChronoUnit.values();
//		Arrays.sort(units , Comparator.comparing(ChronoUnit::getDuration));
//		return Arrays.binarySearch(units, ChronoUnit.DAYS, Comparator.comparing(ChronoUnit::getDuration));
	}

	public static LocalDateTime truncateDateTime(LocalDateTime ldt, ChronoUnit unit) {
//		System.out.println(dt.atOffset(ZoneOffset.ofHours(8)).toEpochSecond() + " " + System.currentTimeMillis()/1000);
		long epochSeconds = ldt.toEpochSecond(ZoneOffset.ofHours(8));
		long truncatedSeconds = epochSeconds - epochSeconds % unit.getDuration().getSeconds();
//		System.out.println(ChronoUnit.DAYS.between(LocalDate.ofEpochDay(0), dt));
//		System.out.println(System.currentTimeMillis()/1000/unit + " " + seconds);
		return LocalDateTime.ofInstant(Instant.ofEpochSecond(truncatedSeconds), ZoneOffset.ofHours(8));
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
		Duration.ofSeconds(3601).getSeconds();

		System.out.println(LocalDate.ofYearDay(2017, 1));
		System.out.println(LocalDate.of(2017, 3, 1).minusDays(1));

		Stream.of("三百零几", "十来万", "一百多万", "一百万", "三四十", "一百七八十万", "一百三四十多万", "十几二十多万")
				.forEach(text -> System.out.println(parseNumber(text)));

		long seconds = ChronoUnit.DECADES.getDuration().getSeconds();

		System.out.println(truncateDateTime(LocalDateTime.now(), ChronoUnit.DAYS));
		System.out.println(truncateDateTime(LocalDateTime.now(), ChronoUnit.MONTHS).getDayOfMonth());
		System.out.println(truncateDateTime(LocalDateTime.now(), ChronoUnit.HOURS).getHour());
		System.out.println(truncateDateTime(LocalDateTime.now(), ChronoUnit.YEARS).getYear());

		System.out.println(LocalDate.now().with(DayOfWeek.MONDAY));
		System.out.println(LocalDate.now().with(DayOfWeek.SUNDAY));
		System.out.println(LocalDate.now().with(ChronoField.ALIGNED_WEEK_OF_MONTH, 1).with(DayOfWeek.MONDAY));
		System.out.println(LocalDate.now().with(WeekFields.of(Locale.CHINA).dayOfWeek(), 1));

//		System.out.println(LocalDate.now().with(ChronoField.PROLEPTIC_MONTH, -1));
		System.out.println(LocalDate.now().plusMonths(-1));
		System.out.println(LocalDate.now().with(ChronoField.DAY_OF_MONTH, 30).plusMonths(-2));
		System.out.println(LocalDate.now().with(ChronoField.DAY_OF_MONTH, 30).plus(-2, ChronoUnit.MONTHS));

	}

}
