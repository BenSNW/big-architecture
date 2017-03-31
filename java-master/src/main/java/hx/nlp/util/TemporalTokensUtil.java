package hx.nlp.util;

import com.google.common.collect.ImmutableMap;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import hx.nlp.parser.temporal.TemporalExpression;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
	public static final String HOUTIAN = "后天";
	public static final String QUNIAN = "去年";
	public static final String JINNIAN = "今年";
	public static final String MINGNIAN = "明年";
	public static final String HOUNIAN = "后年";

	public static final String SHIYI = "十一";


	private static final Set<String> NT_WORDS = new HashSet<>(
			Arrays.asList(QIANTIAN, ZUOTIAN, JINTIAN, MINGTIAN, HOUTIAN, QUNIAN, JINNIAN, MINGNIAN));

	private static final Map<String, Supplier<LocalDate>> ntMappers =
			ImmutableMap.<String, Supplier<LocalDate>>builder()
				.put(QIANTIAN, () -> LocalDate.now().minusDays(2))
				.put(ZUOTIAN, () -> LocalDate.now().minusDays(1))
				.put(JINTIAN, () -> LocalDate.now())
				.put(MINGTIAN, () -> LocalDate.now().plusDays(1))
				.put(HOUTIAN, () -> LocalDate.now().plusDays(2))
				.put(QUNIAN, () -> LocalDate.of( thisYear() - 1, 1, 1)) // 阴历还是阳历
				.put(JINNIAN, () -> LocalDate.of( thisYear(), 1, 1))
				.put(MINGNIAN, () -> LocalDate.of( thisYear() + 1, 1, 1))
				.put(SHIYI, () -> LocalDate.of( thisYear(), 10, 1))
				.build();

	private static final Map<String, Supplier<TemporalExpression>> ntTemporals = ntMappers.entrySet()
			.stream().collect(Collectors.toMap(Map.Entry::getKey, TemporalTokensUtil::getSupplier));

	private static Supplier<TemporalExpression> getSupplier(Map.Entry<String, Supplier<LocalDate>> entry) {
		return () -> TemporalExpression.temporalDate(entry.getKey(), entry.getValue().get());
	}

	public static int thisYear() {
		return LocalDate.now().getYear();
	}

	public static int thisMonth() {
		return LocalDate.now().getMonthValue();
	}

	public LocalDateTime parseNTTokens(List<CoreMap> ntTokens) {
		return parseNTTokens(LocalDateTime.now(), ntTokens);
	}

	public LocalDateTime parseNTTokens(LocalDateTime reference, List<CoreMap> ntTokens) {
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
	}

}
