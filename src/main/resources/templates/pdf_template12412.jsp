<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../../inc_common.jsp" %>
<%@ include file="../../inc_doctype.jsp" %>
<jsp:useBean id="constant" scope="page" class="com.dragon.plat.common.Constant"></jsp:useBean>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>Untitled Document</title>
    <style>
        *{font-size:12px;font-family: "SimSun";}
        body{margin:0;padding:0;}
        h2{position: relative;text-align: center;height:50px;line-height:50px;font-size:20px;font-weight:bold;margin:0;padding:0;}
        h2 img{position: absolute;left:10px;top:10px;}
        table{margin:2px;table-layout:fixed; width: 98%;}
        table{word-break:break-all; }
        table tr{height: 20px;padding: 0px;}
        td{padding: 0px;}
        .p-left{margin:3px 0;}
        p{line-height: 14px;margin: 2px;}
        h3{margin: 2px;height: 14px;}
        .lineHeightText {
            line-height: 20px;
        }
        .textIndent{ text-indent:2em;}
    </style>
</head>

<body style="width: 100%;">
<h2><img width="100px;" height="60px;" src="${fundsSource.companyLogo}" /></h2>
<h2>个人消费借款借据</h2>
<p align="right" style="margin-top: 8px;margin-right:40px;">借据编号：${member.loan.xtSn}</p><br/>
<p class="textIndent">
兹有<u> ${member.realName} </u>（身份证号<u> ${member.creSn} </u>）于<u> ${currYear} </u>年<u> ${currMonth} </u>月<u> ${currDay} </u>日向重庆两江新区同泽小额贷款有限责任公司申请借款用于${member.loan.borrowReason}，并签署了编号为<u> ${member.loan.xtSn} </u>的《个人消费借款合同》，借款信息如下：
</p>
<table border="1" cellspacing="0" cellpadding="0" style="margin-top: 10px;">
    <tr>
        <td colspan="2">借款金额（元）</td>
        <td colspan="2">${member.loan.loanAmt}</td>
        <td colspan="2">借款期限(月)</td>
        <td colspan="2">${member.loan.periods}</td>
        <td colspan="2">借款时间</td>
        <td colspan="2">${currDate}</td>
    </tr>
    <tr>
        <td colspan="2">借款利率</td>
        <td colspan="2">${monthRat}(月)</td>
        <td colspan="2">还款方式</td>
        <td colspan="2">等额本息</td>
        <td colspan="2">每月还款日</td>
        <td colspan="2">${member.loan.perMonthDate}</td>
    </tr>
    <tr>
        <td rowspan="2" colspan="2">银行账户信息</td>
        <td colspan="2">户名</td>
        <td colspan="2">${member.loan.repaymentName}</td>
        <td colspan="2">账号</td>
        <td colspan="4">${member.loan.repaymentAccount}</td>
    </tr>
    <tr>
        <td colspan="2">开户行</td>
        <td colspan="8">${member.paramMap.repaymentBank}</td>
    </tr>
</table>
<p class="textIndent" style="margin-top: 10px;margin-bottom:10px;">借款人签章：<u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日期：<u> ${currDate} </u></p>
<p class="textIndent">附：《还款指引》</p>
<h2 style="height: 45px;">还款指引</h2>
<p style="line-height: 20px">尊敬的&nbsp;${member.realName}&nbsp; <c:if test="${member.gender eq '0'}">先生</c:if> <c:if test="${member.gender eq '1'}">女士</c:if><br/>&nbsp;&nbsp;您于${currDate}签订编号为${member.loan.xtSn}的《个人消费借款合同》，借款金额${member.loan.loanAmt}元，借款期数${member.loan.periods}期，每期还款金额见下表</p>
<table border="1" cellspacing="0" cellpadding="0" style="margin-top: 10px;">
    <tr>
        <td><h3 style="text-align: center;">期数</h3></td>
        <td><h3 style="text-align: center;">还款日期</h3></td>
        <td><h3 style="text-align: center;">借款本金</h3></td>
        <td><h3 style="text-align: center;">借款利息</h3></td>
        <td><h3 style="text-align: center;">每月支付金额（元）</h3></td>
    </tr>
    <c:forEach var="repayment" items="${repaymentDates}" varStatus="paystatus">
    <tr>
        <td><p style="text-align: center;">${repayment.period}</p></td>
        <td><p style="text-align: center;">${repayment.repaymenDate}</p></td>
        <td><p style="text-align: center;">${repayment.planPrincipal}</p></td>
        <td><p style="text-align: center;">${repayment.planServiceFee}</p></td>
        <td><p style="text-align: center;">${repayment.repaymenMoney}</p></td>
    </tr>
    </c:forEach>
</table>
<h3>说明：</h3>
<p>1、若每月支付日大于当月总天数，则当月支付日为当月最后一日。</p>
<p>2、您可以通过以下方式进行还款：</p>
<p>（1）银行代扣还款：请您保证在每个还款日前将足够的还款额存入您指定的账户以便划扣。如您的银行代扣账户有变动，请致电客服热线（<c:choose>
    <c:when test="${member.loan.channel eq '087'}">0238-9696-722</c:when>
    <c:when test="${member.loan.channel eq '083'}">023-86003522</c:when>
    <c:otherwise>4000-353-999</c:otherwise>
</c:choose>）进行变更。若银行代扣还款失败，请按方式（2）进行还款。</p>
<p>（2）拨打
    <c:choose>
        <c:when test="${member.loan.channel eq '087'}">0238-9696-722</c:when>
        <c:when test="${member.loan.channel eq '083'}">023-86003522</c:when>
        <c:otherwise>4000-353-999</c:otherwise>
    </c:choose>
    客服热线进行咨询。</p>
<p>3、以上所列还款金额仅限正常按期按时还款，如您有逾期，按《个人消费借款合同》的约定，您仍需要承担合同约定的逾期费用。</p>
<h3 style="text-align: right;margin-top: 40px;">申请人（签章）：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日期：${currDate}&nbsp;&nbsp;&nbsp;&nbsp;</h3>
</body>
</html>