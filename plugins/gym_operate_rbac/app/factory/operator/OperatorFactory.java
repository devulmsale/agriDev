package factory.operator;

import factory.FactoryBoy;
import factory.ModelFactory;
import models.operator.Operator;

/**
 * 运营商.
 * <p/>
 * User: wangjia
 * Date: 13-5-13
 * Time: 上午10:32
 */
public class OperatorFactory extends ModelFactory<Operator> {
    public final static String DEFAULT_OPERATOR_CODE = Operator.DEFAULT_OPERATOR_CODE;

    @Override
    public Operator define() {
        Operator operator = new Operator();
        operator.name = "上海票务运营平台" + FactoryBoy.sequence(Operator.class);
        operator.code = DEFAULT_OPERATOR_CODE + FactoryBoy.sequence(Operator.class);
        return operator;
    }
}
