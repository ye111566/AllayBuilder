package yedev.allaydev.allaybuilder;
import org.allaymc.api.block.property.type.BlockPropertyType;
import org.allaymc.api.block.type.BlockType;
import org.allaymc.api.block.type.BlockTypes;
import org.allaymc.api.command.Command;
import org.allaymc.api.command.tree.CommandTree;
import org.allaymc.api.entity.interfaces.EntityPlayer;
import org.allaymc.api.form.Forms;
import org.allaymc.api.registry.Registries;
import org.allaymc.api.permission.PermissionGroups;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class BuilderCommand extends Command {
    public BuilderCommand() {
        super("builder", "AllayBuilder");
        getPermissions().forEach(PermissionGroups.OPERATOR::addPermission);
        aliases.add("yb");
        aliases.add("ybuilder");
    }

    @Override
    public void prepareCommandTree(CommandTree tree) {
        tree.getRoot()
                .key("fill")
                .blockType("blockType")
                .blockPropertyValues("blockPropertyValues")

                .exec(context -> {
                    EntityPlayer user=(EntityPlayer) context.getSender();
                    //user.sendMessage("filling...");
                    BlockType<?> blockType = context.getResult(1);
                    List<BlockPropertyType.BlockPropertyValue<?, ?, ?>> blockPropertyValues = context.getResult(2);
                    var blockState = blockPropertyValues.isEmpty() ? blockType.getDefaultState() : blockType.ofState(blockPropertyValues);

                    var posdata=AllayBuilder.posrecorder.get(user.getOriginName());

                    AllayBuilder.fillblock(user,posdata.get("posa"),posdata.get("posb"),blockState,user.getDimension());

                    //user.sendMessage("create the fillblock task");
                    return context.success();
                })

                .key("maintain")
                .blockType("maintainblockType")
                .blockPropertyValues("maintainblockPropertyValues")
                .exec(context -> {
                    EntityPlayer user=(EntityPlayer) context.getSender();
                    //user.sendMessage("filling...");
                    BlockType<?> blockType = context.getResult(1);
                    List<BlockPropertyType.BlockPropertyValue<?, ?, ?>> blockPropertyValues = context.getResult(2);
                    BlockType<?> maintainblockType = context.getResult(4);
                    List<BlockPropertyType.BlockPropertyValue<?, ?, ?>> maintainblockPropertyValues = context.getResult(5);
                    var blockState = blockPropertyValues.isEmpty() ? blockType.getDefaultState() : blockType.ofState(blockPropertyValues);

                    var posdata=AllayBuilder.posrecorder.get(user.getOriginName());
                    if(maintainblockType==null) {
                        AllayBuilder.fillblock(user,posdata.get("posa"),posdata.get("posb"),blockState,user.getDimension());
                    }else {
                        var maintainblockState = maintainblockPropertyValues.isEmpty() ? maintainblockType.getDefaultState() : maintainblockType.ofState(maintainblockPropertyValues);
                        AllayBuilder.maintainblock(user,posdata.get("posa"),posdata.get("posb"),blockState,maintainblockState,user.getDimension());
                    }
                    //user.sendMessage("create the fillblock task");
                    return context.success();
                })
                .up(3)
                .key("replace")
                .blockType("bereplacedblockType")
                .blockPropertyValues("bereplacedblockPropertyValues")
                .exec(context -> {
                    EntityPlayer user=(EntityPlayer) context.getSender();
                    //user.sendMessage("filling...");
                    BlockType<?> blockType = context.getResult(1);
                    List<BlockPropertyType.BlockPropertyValue<?, ?, ?>> blockPropertyValues = context.getResult(2);
                    BlockType<?> bereplacedblockType = context.getResult(4);
                    List<BlockPropertyType.BlockPropertyValue<?, ?, ?>> bereplacedblockPropertyValues = context.getResult(5);
                    var blockState = blockPropertyValues.isEmpty() ? blockType.getDefaultState() : blockType.ofState(blockPropertyValues);

                    var posdata=AllayBuilder.posrecorder.get(user.getOriginName());
                    if(bereplacedblockType==null) {
                        AllayBuilder.fillblock(user,posdata.get("posa"),posdata.get("posb"),blockState,user.getDimension());
                    }else {
                        var bereplacedblockState = bereplacedblockPropertyValues.isEmpty() ? bereplacedblockType.getDefaultState() : bereplacedblockType.ofState(bereplacedblockPropertyValues);
                        AllayBuilder.replaceblock(user,posdata.get("posa"),posdata.get("posb"),blockState,bereplacedblockState,user.getDimension());
                    }
                    //user.sendMessage("create the fillblock task");
                    return context.success();
                })
                .up(3)
                .key("keep").optional()
                .exec(context -> {
                    EntityPlayer user=(EntityPlayer) context.getSender();
                    //user.sendMessage("filling...");
                    BlockType<?> blockType = context.getResult(1);
                    List<BlockPropertyType.BlockPropertyValue<?, ?, ?>> blockPropertyValues = context.getResult(2);
                    var blockState = blockPropertyValues.isEmpty() ? blockType.getDefaultState() : blockType.ofState(blockPropertyValues);

                    var posdata=AllayBuilder.posrecorder.get(user.getOriginName());

                    AllayBuilder.replaceblock(user,posdata.get("posa"),posdata.get("posb"),blockState, BlockTypes.AIR.getDefaultState(),user.getDimension());

                    //user.sendMessage("create the fillblock task");
                    return context.success();
                })

                .root()
                .key("save")
                .msg("structure name").optional()
                .exec(context -> {
                            String structure_name=context.getResult(1);
                            EntityPlayer user = (EntityPlayer) context.getSender();
                            if(!structure_name.isBlank()) {
                                var posdata = AllayBuilder.posrecorder.get(user.getOriginName());
                                if (posdata.get("posa") != null && posdata.get("posb") != null) {
                                    AllayBuilder.structuresave(user, structure_name, posdata.get("posa"), posdata.get("posb"), user.getDimension());

                                } else {
                                    user.sendMessage("无效的选区");
                                }
                            }
                            else{

                                Forms.custom()
                                        .title("输入保存结构的名字")
                                        .input("")
                                        .onResponse((List<String> response) -> {
                                            String structname = response.get(0);
                                            if (structname == null) {
                                                user.sendMessage("无效的名字");
                                            } else {
                                                var posdata = AllayBuilder.posrecorder.get(user.getOriginName());
                                                if (posdata.get("posa") != null && posdata.get("posb") != null) {
                                                    AllayBuilder.structuresave(user, structname, posdata.get("posa"), posdata.get("posb"), user.getDimension());

                                                } else {
                                                    user.sendMessage("无效的选区");
                                                }
                                            }
                                        })
                                        .sendTo(user);


                            }
                            return context.success();
                        }
                ).root()
                .key("load")
                .msg("structure name").optional()
                .exec(context ->{

                    EntityPlayer user=(EntityPlayer) context.getSender();
                    String structurename=context.getResult(1);
                    if(structurename.isBlank()) {
                        System.out.println("use structureload menu");
                        List<String> structurelist = new ArrayList<>(new LinkedHashSet<>(AllayBuilder.getuseable_structures()));
                        var theform=Forms.simple();
                        theform.title("选择结构");
                        for(String eachstructurename : structurelist) {
                            theform
                                    .button(eachstructurename)
                                    .onClick(button -> {
                                        Registries.COMMANDS.execute(user, "yb load "+eachstructurename);
                                    });

                        }
                        theform.sendTo(user);
                    }else{
                        user.sendMessage("load "+structurename);
                        AllayBuilder.structureload(user,structurename);
                    }
                    return context.success();
                })
                .root()
                .key("show_load_direction")
                .bool("show_load_direction")
                .exec(context->{
                    Boolean show_load_direction =context.getResult(1);
                    EntityPlayer user=(EntityPlayer) context.getSender();
                    AllayBuilder.debugarrow.put(user,show_load_direction);
                    return context.success();
                })






        ;
    }
}