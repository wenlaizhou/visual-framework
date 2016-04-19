package cn.framework.core;

import cn.framework.core.container.FrameworkContainer;
import cn.framework.core.utils.*;
import org.apache.commons.cli.*;
import org.apache.zookeeper.ZooKeeperMain;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

import java.io.File;
import java.util.Properties;

/**
 * project code
 * package cn.framework.core
 * create at 16/3/25 下午5:43
 *
 * @author wenlai
 */
public final class FrameworkStart {

    public static void main(String[] args) throws Exception {
        START(args);
    }


    /**
     * 启动项 <br>
     * <p>
     * start : start the framework
     * generate-qrcode :
     * help : print help info
     * validate : validate config file is available
     * print-properties : print all project property
     * D : property input
     * property-file : property file path
     *
     * @param args 参数
     *
     * @throws Exception
     */
    public static void START(String[] args) throws Exception {
        Projects.init();
        Options opts = buildOptions();
        CommandLine cmd = buildCommandLines(args, opts);
        processCommand(cmd, opts);
    }

    /**
     * 配置文件检测
     *
     * @param confPath
     *
     * @return
     */
    public static boolean checkConfig(String confPath) {
        return true;
    }


    /**
     * 创建命令行参数
     *
     * @return
     */
    private static Options buildOptions() {
        try {
            Options options = new Options();

            // add start opts
            options.addOption(Option.builder("start").hasArg(true).argName("config-path").desc("启动框架服务,传入配置文件路径,包含属性解析").build());

            // add qr opts
            options.addOption(Option.builder("genQrcode").argName("property=value").hasArg(true).numberOfArgs(2).valueSeparator().valueSeparator('=').optionalArg(true).desc("生成二维码:file=<file-path> value=<value>").build());

            // add qr decode
            options.addOption(Option.builder("decodeQrcode").hasArg(true).argName("file-path").desc("解析二维码").build());

            // add property support
            options.addOption(Option.builder("D").valueSeparator().desc("属性key=value对,可以多次赋值").argName("key=value").numberOfArgs(2).valueSeparator('=').optionalArg(true).build());

            // add check
            options.addOption(Option.builder("check").desc("检查配置项是否有异常").argName("config-path").hasArg().build());

            // add print project property
            options.addOption(Option.builder("print").desc("打印所有系统默认属性参数").build());

            // add zookeeper client
            options.addOption(Option.builder("zkCli").desc("开启zookeper客户端").build());

            // add property file
            // options.addOption(Option.builder("properties").desc("k-v属性文件路径").hasArg().argName("property-file-path").build());
            // add class loader class name
            options.addOption(Option.builder("customClass").desc("设置ClassLoader使用的类名,如果分离部署,则应该设置该属性").argName("className").hasArg().build());
            options.addOption(Option.builder("digest").desc("针对zookeeper密码加密").argName("id").hasArg().build());
            options.addOption(Option.builder("h").desc("打印帮助信息").build());
            options.addOption(Option.builder("help").desc("打印帮助信息").build());
            return options;
        }
        catch (Exception x) {
            x.printStackTrace();
            return null;
        }
    }

    /**
     * 处理命令
     *
     * @param cmd
     * @param options
     *
     * @throws Exception
     */
    private static void processCommand(CommandLine cmd, Options options) throws Exception {

        if (cmd == null || cmd.hasOption("help") || cmd.hasOption("h") || cmd.getOptions() == null || cmd.getOptions().length == 0) {
            printHelp(options);
            return;
        }

        if (cmd.hasOption("D")) {
            Properties properties = cmd.getOptionProperties("D");
            if (properties != null) {
                for (Object key : properties.keySet()) {
                    Property.set(key.toString(), properties.get(key) != null ? Property.fill(properties.get(key).toString()) : Strings.EMPTY);
                }
            }
        }

        if (cmd.hasOption("customClass")) {
            String customClassName = cmd.getOptionValue("customClass");
            if (Strings.isNotNullOrEmpty(customClassName) && Class.forName(customClassName) != null) {
                Property.set(Property.MAIN_CLASS, customClassName);
            }
        }

        if (cmd.hasOption("print")) {
            Property.printAll();
        }

        if (cmd.hasOption("genQrcode")) {
            Properties properties = cmd.getOptionProperties("genQrcode");
            if (properties != null && properties.size() > 0) {
                try {
                    QRCode.generateQRCode(properties.getProperty("value"), new File(Property.fill(properties.getProperty("file"))));
                    System.out.println("二维码生成成功");
                }
                catch (Exception x) {
                    x.printStackTrace();
                    printHelp(options);
                }
            }
            else {
                printHelp(options);
                System.err.println("参数错误");
            }
            return;
        }

        if (cmd.hasOption("digest")) {
            String value = cmd.getOptionValue("digest");
            System.out.println(DigestAuthenticationProvider.generateDigest(value));
            return;
        }

        if (cmd.hasOption("decodeQrcode")) {
            try {
                String fileName = cmd.getOptionValue("decodeQrcode");
                if (Strings.isNotNullOrEmpty(fileName) && Files.exist(fileName = Property.fill(fileName))) {
                    System.out.println("二维码识别完毕:");
                    System.out.println(QRCode.readQRCode(fileName));
                }
                else {
                    System.err.println("file not exist!");
                    printHelp(options);
                }
            }
            catch (Exception x) {
                x.printStackTrace();
                printHelp(options);
            }
            return;
        }

        if (cmd.hasOption("zkCli")) {
            ZooKeeperMain.main(cmd.getArgs());
        }

        if (cmd.hasOption("start")) { // start framework
            String confPath = cmd.getOptionValue("start");
            if (Strings.isNullOrEmpty(confPath) || !checkConfig(confPath)) {
                printHelp(options);
                System.err.println("配置文件不正确");
                return;
            }
            FrameworkContainer container = new FrameworkContainer(Property.fill(confPath));
            container.init();
            container.start();
            return;
        }
    }

    /**
     * 打印帮助信息
     *
     * @param opts
     */
    private static void printHelp(Options opts) {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp("start", "项目目录如下 :\n project/conf -- 配置文件目录\n project/log -- 日志目录 \n project/bin -- 启动脚本 start存放目录 \n project/lib -- 依赖库目录\n\n", opts, "\n\npowered by wenlai : wenlai_zhou@126.com", true);
    }

    /**
     * 创建命令行工具
     *
     * @param args
     * @param options
     *
     * @return
     */
    private static CommandLine buildCommandLines(String[] args, Options options) {
        try {
            CommandLineParser parser = new DefaultParser();
            return parser.parse(options, args);
        }
        catch (Exception x) {
            x.printStackTrace();
        }
        return null;
    }
}
