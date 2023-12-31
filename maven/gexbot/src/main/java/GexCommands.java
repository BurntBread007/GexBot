import java.awt.Color;

import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.entity.message.embed.EmbedBuilder;

public class GexCommands {
    static int countQuip, countAIReply, countGex = 0;

    public static EmbedBuilder stats() {
        EmbedBuilder embed = new EmbedBuilder();
        // Calculates elapsed runtime
        long days, hours, minutes, seconds;
        seconds = (System.currentTimeMillis()-MessageListener.firstTime) / 1000;
        minutes = seconds / 60;
        hours = minutes / 60;
        days = hours / 24;
        seconds %= 60;
        minutes %= 60;
        hours %= 24;

        // Calculates memory usage
        com.sun.management.OperatingSystemMXBean os = (com.sun.management.OperatingSystemMXBean)
        java.lang.management.ManagementFactory.getOperatingSystemMXBean();
        double totalMemGB = (double)os.getTotalPhysicalMemorySize() / 1073741824;
        double usedMemGB = (double)Runtime.getRuntime().maxMemory() / 1073741824;

        String memoryText = String.format("%4.2f", usedMemGB)+" GB / "+String.format("%4.2f", totalMemGB)+" GB";
        String memoryBar = "";
        int free = (int)(usedMemGB / (totalMemGB / 12));
        int left = 12 - free;
        for(int i = 0; i < free; i++)
            memoryBar += "\u2B1C";
        for(int i = 0; i < left; i++)
            memoryBar += "\u2B1B";

        // Builds stats embed page
        embed
            .setAuthor("GexBot "+GexBot.VERSION, "http://github.com/burntbread007/GexBot/", "https://cdn.discordapp.com/avatars/1154245369488756778/cc6e4baf92995a63317c1dad9e265d23.webp")
            .setTitle("Global Statistics")
            .setColor(Color.GREEN)
            .addField("Total Messages Written:", "")
            .addInlineField("Quips", GexCommands.countQuip+"")
            .addInlineField("AI Replies", GexCommands.countAIReply+"")
            .addInlineField("Gex References", GexCommands.countGex+"")
            .addField("Current AI Model", GexBot.AI_MODEL)
            .addField("Elapsed Runtime", days+" days, "+hours+" hours, "+minutes+" minutes, "+seconds+" seconds")
            .addField("System Memory Usage", memoryBar+"\n"+memoryText)
            .setFooter("\"It's tail time!\" - Gexy")
        ;
        return(embed);
    }

    public static EmbedBuilder model() {
        while(GexGPT.replyQueue.size() != 0) {
            try { Thread.sleep(1000); }
            catch (Exception e) {}
        }
        EmbedBuilder embed = new EmbedBuilder();
        embed
            .setTitle("AI Chat Model List")
            .setColor(Color.GREEN)
            .addField("", "\n- Falcon\n- Wizard\n- Llama\n- Hermes\n- Uncensored")
            .addField("Type \""+GexBot.PREFIX+"model NAME\" to change the model.", "")
        ;
        return embed;
    }

    public static EmbedBuilder queue() {
        EmbedBuilder embed = new EmbedBuilder();
        embed
            .setTitle("Upcoming messages in my AI Chat Queue:")
            .setColor(Color.GREEN)
        ;

        if(GexGPT.replyQueue.size() == 0) { embed.addField("", "There are no prompts for me to answer!"); }
        else {
            int num;
            String indexes = "";
            if(GexGPT.replyQueue.size() < GexGPT.maxReplyPrint) { num = GexGPT.replyQueue.size(); }
            else { num = GexGPT.maxReplyPrint; }
            for(int i = 0; i < num; i++)
                indexes += ""+(i+1)+"\n";
            embed
                .addInlineField("Index", indexes)
                .addInlineField("Prompt", GexGPT.printPrompts())
                .setFooter("Total Prompts in Queue: "+GexGPT.replyQueue.size())
            ;
        }
        return embed;
    }

    public static String model(String command, String msg) {
        String temp = GexBot.convertModelName(msg);
        if(temp.equals("")) {
            System.out.println("[GexCommands] AI chat model could not be changed to \""+msg+"\".");
            return "AI chat model could not be changed to "+msg+"!";
        } else {
            GexBot.AI_MODEL = temp;
            GexGPT.loadModel();
            System.out.println("[GexCommands] AI chat model successfully changed to \""+msg+"\".");
            return "AI chat model successfully changed to "+msg+"!";
        }
    }

    public static String quip() {
        String sentence, name, result;
        int splice;
        double random;

        random = Math.random() * GexBot.sentenceFileArr.size();
        sentence = GexBot.sentenceFileArr.get((int)random);
        splice = sentence.indexOf("_");

        random = Math.random() * GexBot.nameFileArr.size();
        name = GexBot.nameFileArr.get((int)random);

        result = sentence.substring(0, splice) + name + sentence.substring(splice+1);
        countQuip++;
        return result;
    }

    public static String time() {
        return "It's tail time! <:GexSmirk:1154237747544997930>";
    }

    public static String reply() {
        double random = Math.random() * GexBot.mentionFileArr.size();
        countGex++;
        return (GexBot.mentionFileArr.get((int)random));
    }

    public static String context() {
        GexGPT.clearContext();
        System.out.println("[GexCommands] AI context array has been clearned.");
        return "AI context is cleared!";
    }

    public static String status(String msg) {
        GexBot.api.updateActivity( ActivityType.PLAYING, msg);
        System.out.println("[GexCommands] Activity status changed to \""+msg+"\".");
        return "Status successfully changed! <:GexSmirk:1154237747544997930>";
    }

    public static String prefix(String msg) {
            GexBot.PREFIX = msg.substring(0, 1);
            System.out.println("[MessageListener] Command prefix changed to "+GexBot.PREFIX+".");
            return "Changed command prefix to "+GexBot.PREFIX+".";
    }

    public static void shutdown() {
        System.out.println("[GexCommands] Requested shutdown by admin...");
        System.exit(0);
    }

    public static void restart() {
        System.out.println("[GexCommands] Requested intentional exception by admin...");
        System.exit(1);
    }
}