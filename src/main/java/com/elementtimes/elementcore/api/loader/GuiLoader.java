package com.elementtimes.elementcore.api.loader;

import com.elementtimes.elementcore.api.ECModElements;
import com.elementtimes.elementcore.api.annotation.ModContainer;
import com.elementtimes.elementcore.api.annotation.result.ScreenWrapper;
import com.elementtimes.elementcore.api.helper.FindOptions;
import com.elementtimes.elementcore.api.helper.ObjHelper;
import com.elementtimes.elementcore.api.helper.RefHelper;
import com.elementtimes.elementcore.api.interfaces.invoker.Invoker;
import com.elementtimes.elementcore.api.utils.CommonUtils;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.forgespi.language.ModFileScanData;

import java.lang.annotation.ElementType;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.Supplier;

/**
 * @author luqin2007
 */
public class GuiLoader {

    public static void load(ECModElements elements) {
        loadContainerType(elements);
        if (CommonUtils.isClient()) {
            loadContainerScreen(elements);
        }
    }

    private static void loadContainerType(ECModElements elements) {
        ObjHelper.stream(elements, ModContainer.class).forEach(data -> {
            String name = ObjHelper.getDefault(data);
            switch (data.getTargetType()) {
                case METHOD:
                    ObjHelper.findClass(elements, data.getClassType()).ifPresent(aClass -> {
                        String method = ObjHelper.getMemberName(data);
                        try {
                            Method m = aClass.getMethod(method, int.class, PlayerInventory.class);
                            if (!Modifier.isPublic(m.getModifiers())) {
                                m.setAccessible(true);
                            }
                            ContainerType ct = new ContainerType((id, inv) -> {
                                try {
                                    return (Container) m.invoke(null, id, inv);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    return null;
                                }
                            });
                            ObjHelper.setRegisterName(ct, name, data, elements);
                            if (CommonUtils.isClient()) {
                                loadContainerScreen(elements, data, ct);
                            }
                            elements.containerTypes.add(ct);
                        } catch (NoSuchMethodException e) {
                            try {
                                Method m = aClass.getMethod(method, int.class, PlayerInventory.class, PacketBuffer.class);
                                if (!Modifier.isPublic(m.getModifiers())) {
                                    m.setAccessible(true);
                                }
                                ContainerType ct = new ContainerType((IContainerFactory) (id, inv, data1) -> {
                                    try {
                                        return (Container) m.invoke(null, id, inv, data1);
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                        return null;
                                    }
                                });
                                ObjHelper.setRegisterName(ct, name, data, elements);
                                if (CommonUtils.isClient()) {
                                    loadContainerScreen(elements, data, ct);
                                }
                                elements.containerTypes.add(ct);
                            } catch (NoSuchMethodException ignored) { }
                        }
                    });
                    break;
                case FIELD:
                    FindOptions option = new FindOptions().withReturns(ContainerType.class).withTypes(ElementType.FIELD);
                    ObjHelper.find(elements, data, option).ifPresent(obj -> {
                        ContainerType type = (ContainerType) obj;
                        ObjHelper.setRegisterName(type, name, data, elements);
                        if (CommonUtils.isClient()) {
                            loadContainerScreen(elements, data, type);
                        }
                        elements.containerTypes.add(type);
                    });
                    break;
                case TYPE:
                    ObjHelper.findClass(elements, data.getClassType()).filter(ContainerType.class::isAssignableFrom).ifPresent(aClass -> {
                        try {
                            Constructor<?> c = aClass.getDeclaredConstructor(int.class, PlayerInventory.class, PacketBuffer.class);
                            if (!Modifier.isPublic(c.getModifiers())) {
                                c.setAccessible(true);
                            }
                            ContainerType type = new ContainerType((IContainerFactory) (windowId, inv, data12) -> {
                                try {
                                    return (Container) c.newInstance(windowId, inv, data12);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    return null;
                                }
                            });
                            ObjHelper.setRegisterName(type, name, data, elements);
                            if (CommonUtils.isClient()) {
                                loadContainerScreen(elements, data, type);
                            }
                            elements.containerTypes.add(type);
                        } catch (NoSuchMethodException e) {
                            try {
                                Constructor<?> c = aClass.getDeclaredConstructor(int.class, PlayerInventory.class);
                                if (!Modifier.isPublic(c.getModifiers())) {
                                    c.setAccessible(true);
                                }
                                ContainerType type = new ContainerType((windowId, inv) -> {
                                    try {
                                        return (Container) c.newInstance(windowId, inv);
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                        return null;
                                    }
                                });
                                ObjHelper.setRegisterName(type, name, data, elements);
                                if (CommonUtils.isClient()) {
                                    loadContainerScreen(elements, data, type);
                                }
                                elements.containerTypes.add(type);
                            } catch (NoSuchMethodException ignored) { }
                        }
                    });
                    break;
                default:
            }
        });
    }

    @OnlyIn(Dist.CLIENT)
    private static void loadContainerScreen(ECModElements elements, ModFileScanData.AnnotationData data, ContainerType type) {
        Invoker<net.minecraft.client.gui.screen.Screen> screen =
                RefHelper.invoker(elements, data.getAnnotationData().get("screen"), Invoker.empty(), Container.class, PlayerInventory.class, ITextComponent.class);
        elements.containerScreens.add(new ScreenWrapper(() -> type, (a, b, c) -> screen.invoke(a, b, c)));
    }

    @OnlyIn(Dist.CLIENT)
    private static void loadContainerScreen(ECModElements elements) {
        Class<?> screen = net.minecraft.client.gui.screen.Screen.class;
        ObjHelper.stream(elements, ModContainer.Screen.class).forEach(data -> {
            Supplier<ContainerType> type = RefHelper.getter(elements, ObjHelper.getDefault(data), ContainerType.class);
            switch (data.getTargetType()) {
                case METHOD:
                    ObjHelper.findClass(elements, data.getClassType()).ifPresent(aClass -> {
                        String method = ObjHelper.getMemberName(data);
                        try {
                            Method m = aClass.getMethod(method, Container.class, PlayerInventory.class, ITextComponent.class);
                            if (!Modifier.isPublic(m.getModifiers())) {
                                m.setAccessible(true);
                            }
                            elements.containerScreens.add(new ScreenWrapper(type, (a, b, c) -> {
                                try {
                                    return m.invoke(null, a, b, c);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    return null;
                                }
                            }));
                        } catch (NoSuchMethodException ignored) { }
                    });
                    break;
                case TYPE:
                    ObjHelper.findClass(elements, data.getClassType()).filter(screen::isAssignableFrom).ifPresent(aClass -> {
                        try {
                            Constructor<?> m = aClass.getDeclaredConstructor(Container.class, PlayerInventory.class, ITextComponent.class);
                            if (!Modifier.isPublic(m.getModifiers())) {
                                m.setAccessible(true);
                            }
                            elements.containerScreens.add(new ScreenWrapper(type, (a, b, c) -> {
                                try {
                                    return m.newInstance(a, b, c);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    return null;
                                }
                            }));
                        } catch (NoSuchMethodException ignored) { }
                    });
                    break;
                default:
            }
        });
    }
}
