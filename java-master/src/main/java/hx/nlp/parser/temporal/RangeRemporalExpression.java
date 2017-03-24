package hx.nlp.parser.temporal;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Created by zhipeng.wang on 03/23 2017.
 */
@Data
@NoArgsConstructor
public class RangeRemporalExpression {

	private LocalDateTime start, end;

	public RangeRemporalExpression(String text) {
//		super(text);
	}

}
