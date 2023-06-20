package perfanalyzer.agent.bytebuddy.jdbc;

import static net.bytebuddy.matcher.ElementMatchers.hasSignature;
import static net.bytebuddy.matcher.ElementMatchers.isAbstract;
import static net.bytebuddy.matcher.ElementMatchers.not;

import java.security.ProtectionDomain;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.bytebuddy.agent.builder.AgentBuilder.Transformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.method.MethodDescription.SignatureToken;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.utility.JavaModule;

public class StatementTransformer implements Transformer {

	public static TypeDescription T_VOID = TypeDescription.ForLoadedType.of(void.class);
	public static TypeDescription T_STRING = TypeDescription.ForLoadedType.of(String.class);
	public static TypeDescription T_STRINGARR = TypeDescription.ForLoadedType.of(String[].class);
	public static TypeDescription T_INT = TypeDescription.ForLoadedType.of(int.class);
	public static TypeDescription T_INTARR = TypeDescription.ForLoadedType.of(int[].class);
	public static TypeDescription T_BOOLEAN = TypeDescription.ForLoadedType.of(boolean.class);
	public static TypeDescription T_RS = TypeDescription.ForLoadedType.of(ResultSet.class);

	@Override
	public Builder<?> transform(Builder<?> builder, TypeDescription typeDescription, ClassLoader classLoader,
			JavaModule module, ProtectionDomain protectionDomain) {
		// Statement execute with sql as parameter
		List<SignatureToken> sigList = new ArrayList<>();
		sigList.add(new SignatureToken("execute", T_BOOLEAN, T_STRING));
		sigList.add(new SignatureToken("execute", T_BOOLEAN, T_STRING, T_INT));
		sigList.add(new SignatureToken("execute", T_BOOLEAN, T_STRING, T_INTARR));
		sigList.add(new SignatureToken("execute", T_BOOLEAN, T_STRING, T_STRINGARR));
		sigList.add(new SignatureToken("executeUpdate", T_INT, T_STRING));
		sigList.add(new SignatureToken("executeUpdate", T_INT, T_STRING, T_INT));
		sigList.add(new SignatureToken("executeUpdate", T_INT, T_STRING, T_INTARR));
		sigList.add(new SignatureToken("executeUpdate", T_INT, T_STRING, T_STRINGARR));
		sigList.add(new SignatureToken("executeQuery", T_RS, T_STRING));
		for (SignatureToken sig : sigList) {
			builder = builder.method(hasSignature(sig).and(not(isAbstract())))
					.intercept(Advice.to(StatementExecuteSqlAdvice.class));
		}

		// PreparedStatement/CallableStatement
		sigList = new ArrayList<>();
		sigList.add(new SignatureToken("execute", T_BOOLEAN));
		sigList.add(new SignatureToken("executeUpdate", T_INT));
		sigList.add(new SignatureToken("executeQuery", T_RS));
		sigList.add(new SignatureToken("executeBatch", T_INTARR));
		for (SignatureToken sig : sigList) {
			builder = builder.method(hasSignature(sig).and(not(isAbstract())))
					.intercept(Advice.to(StatementExecuteAdvice.class));
		}

		// addBatch
		builder = builder.method(
				hasSignature(new SignatureToken("addBatch", T_VOID, T_STRING)).and(not(isAbstract())))
				.intercept(Advice.to(StatementAddBatchSqlAdvice.class));
		builder = builder
				.method(hasSignature(new SignatureToken("addBatch", T_VOID)).and(not(isAbstract())))
				.intercept(Advice.to(StatementAddBatchAdvice.class));

		return builder;
	}

}
