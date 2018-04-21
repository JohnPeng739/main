package org.mx.test;

import org.mx.hanlp.TextExtracter;
import org.mx.hanlp.impl.TextExtracterImpl;

import java.util.List;

public class TestExtracter {
    public static void main(String[] args) throws Exception {
        TextExtracter extracter = new TextExtracterImpl();
        String content = "算法可大致分为基本算法、数据结构的算法、数论算法、计算几何的算法、图的算法、动态规划以及数值分析、加密算法、排序算法、检索算法、随机化算法、并行算法、厄米变形模型、随机森林算法。\n" +
                "算法可以宽泛的分为三类，\n" +
                "一，有限的确定性算法，这类算法在有限的一段时间内终止。他们可能要花很长时间来执行指定的任务，但仍将在一定的时间内终止。这类算法得出的结果常取决于输入值。\n" +
                "二，有限的非确定算法，这类算法在有限的时间内终止。然而，对于一个（或一些）给定的数值，算法的结果并不是唯一的或确定的。\n" +
                "三，无限的算法，是那些由于没有定义终止定义条件，或定义的条件无法由输入的数据满足而不终止运行的算法。通常，无限算法的产生是由于未能确定的定义终止条件。";
        String summary = extracter.summary(content);
        System.out.println(summary);
        List<String> keywords = extracter.keywords(content);
        System.out.println(keywords);

        content = "法新社消息称，日本防卫大臣小野寺五典表示，日本对朝鲜停止核试验和洲际导弹发射的承诺并不满意，警告说日本将继续对平壤施加最大压力。\n" +
                "\n" +
                "此前，特朗普发推称，“朝鲜同意暂停所有核试验并关闭一个主要的核试验基地。这对朝鲜和世界来说都是一个非常好的消息——这是一个巨大的进步!期待我们的峰会”。\n" +
                "\n" +
                "金正恩在20日举行的劳动党中央委员会第七届第三次全体会议上宣布，朝鲜将从21日开始不再进行任何核试验和洲际弹道导弹发射，废弃朝鲜北部核试验场。金正恩表示，只要朝鲜不受核威胁挑衅，朝鲜绝对不使用核武器，不泄露核武器和核技术。\n" +
                "\n" +
                "金正恩称，朝鲜将集中全部力量发展经济，提高人民生活水平。为营造对发展经济有利的国际环境，维护朝鲜半岛和世界和平，朝鲜将与周边国家和国际社会积极展开紧密联系和对话。";
        summary = extracter.summary(content);
        System.out.println(summary);
        keywords = extracter.keywords(content);
        System.out.println(keywords);

        content = "中兴通讯20日针对美国商务部的出口权限禁止令发表声明，称美国在相关调查尚未结束前，执意施以最严厉的制裁，对中兴通讯极不公平，中兴不能接受。\n" +
                "\n" +
                "中兴在声明中同时表示：\n" +
                "\n" +
                "中兴通讯不会放弃通过沟通对话解决问题的努力，也有决心通过一切法律允许的手段维护自身合法权益、维护全体员工和股东的合法权益，履行对全球客户、消费者用户、合作伙伴及供应商的责任。\n" +
                "\n" +
                "美方对中兴“下重手”，是近期一大新闻热点。外界纷纷猜测，美方还有“后招”，包括限制本国通信运营商购买中国产设备、禁止中国企业在美提供云计算等高科技服务。\n" +
                "\n" +
                "这件事的道理其实很简单。打个比方，高速公路是美国的，中国拥有最好的车队，但美国不让你上路，车队再好也白搭。更何况，美国现如今是在高技术出口和进口的双向车道同时设障。\n" +
                "\n" +
                "令外界关注的是，中方会怎样受损，中方又会如何反制？不妨看得更长远一些：面对别人“卡脖子”的招数，最有力的反制就是反思，知难而上，中国必须报以更多中国改革、中国创新！\n" +
                "\n" +
                "第一，中国人要对技术源头端奋起直追。\n" +
                "\n" +
                "即便是在国际供应链日益稳固、中西方利益深度绑定的今天，“西方创新＋中国应用”的模式也不会恒久不变。这是我们必须清醒面对的现实。\n" +
                "\n" +
                "一方面，有远见、有担当的中国企业家必将提升研发投入；另一方面，技术类资产估值将被推高。在产业需求和投资偏好的双重加持下，中长期会吸引更多技术要素“花落”中国。\n" +
                "\n" +
                "美国越是“卡脖子”，中国越会加速厚植创新土壤，为创新企业提供更加优厚的成长环境。中国也会加快科技创投市场的构建和改革，进一步拓展资本市场对创新试错、容错的涵养空间。\n" +
                "\n" +
                "第二，中国人要加速接轨国际高标准。\n" +
                "\n" +
                "中国企业加速构建满足新时代要求的现代企业管理制度，以高标准接轨、对标国际惯例，应对日益苛严的外部审查合规压力。\n" +
                "\n" +
                "熟知国际规则、会打国际官司是中国企业“走出去”的标配技能。外部环境的变化，对企业内控管理和外部危机处置能力提出了更高要求。\n" +
                "\n" +
                "说到底，今后在西方监管机构的特殊“关照”下，中企既需要提升“不怕事”的底气，也需要提升“能平事”的本领。只有这样，不管是“狼来了”还是“狼走了”，都能从容应对。\n" +
                "\n" +
                "只要中国融入世界的意愿坚定如初，只要经济全球化进程不发生逆转，只要市场机制依然发挥作用，就没有任何力量能够阻挡中国追赶世界科技前沿的步伐，也没有任何力量能够掐断中外产经交流的脉搏。\n" +
                "\n" +
                "外部的大风大浪，只会更激发中国走出一批对国际规则懂行、处置手段老道、渠道资源畅通的国际化大公司；只会更激发中国人自主创新的小宇宙和大志向；只会更激发中国加速在高技术研发领域深化改革、扩大开放的历史进程。\n" +
                "\n" +
                "最后，也希望中兴这家在中国成长起来的全球化企业，能如声明中说的那样，凝聚全体员工，坚定信心、团结一致、渡过难关。";
        summary = extracter.summary(content);
        System.out.println(summary);
        keywords = extracter.keywords(content, 10);
        System.out.println(keywords);

        content = "云龙园馋村联丰电器厂前面100米\n" +
                " 电线着火  请辖区中队立即调派周边微型消防站增援  \n" +
                " 抢险救援";
        summary = extracter.summary(content);
        System.out.println(summary);
        keywords = extracter.keywords(content);
        System.out.println(keywords);

        content = " 横河东上河村虹桥菜场\",\n" +
                "   一层小房子着火。被自行扑灭,\n" +
                "   其他";
        summary = extracter.summary(content);
        System.out.println(summary);
        keywords = extracter.keywords(content);
        System.out.println(keywords);

        content = " 黄鹂新村352号   马蜂窝,\n" +
                "马蜂窝\n" +
                " 摘除蜂窝";
        summary = extracter.summary(content);
        System.out.println(summary);
        keywords = extracter.keywords(content);
        System.out.println(keywords);
    }
}
