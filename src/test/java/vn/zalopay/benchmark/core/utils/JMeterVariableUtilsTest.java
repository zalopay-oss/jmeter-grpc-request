package vn.zalopay.benchmark.core.utils;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.gui.GuiPackage;
import org.apache.jmeter.gui.JMeterGUIComponent;
import org.apache.jmeter.gui.tree.JMeterTreeListener;
import org.apache.jmeter.gui.tree.JMeterTreeModel;
import org.apache.jmeter.testelement.TestElement;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import vn.zalopay.benchmark.GRPCSampler;
import vn.zalopay.benchmark.core.BaseTest;
import vn.zalopay.benchmark.util.JMeterVariableUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.*;

public class JMeterVariableUtilsTest extends BaseTest {

    @Test(expectedExceptions = InvocationTargetException.class)
    public void testCantInstanceNewObject()
            throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<?>[] ctors = JMeterVariableUtils.class.getDeclaredConstructors();
        Assert.assertEquals(1, ctors.length, "Utility class should only have one constructor");
        Constructor<?> ctor = ctors[0];
        ctor.setAccessible(true);
        ctor.newInstance();
        Assert.fail("Utility class constructor should be private");
    }

    @Test(singleThreaded = true)
    public void testCanGetValueReplacerInExistingGuiPack() {
        JMeterTreeListener jMeterTreeListener = new JMeterTreeListener();
        JMeterTreeModel jMeterTreeModel = new JMeterTreeModel();
        GuiPackage.initInstance(jMeterTreeListener, jMeterTreeModel);
        GRPCSampler grpcSampler = new GRPCSampler();
        grpcSampler.setDeadline("${deadline}");
        TestElement configNode =
                GuiPackage.getInstance()
                        .createTestElement(
                                "vn.zalopay.benchmark.core.utils.JMeterVariableUtilsTest$DummyGUIComponent");
        TestElement grpcNode =
                GuiPackage.getInstance()
                        .createTestElement(
                                "vn.zalopay.benchmark.core.utils.JMeterVariableUtilsTest$DummyNotMatchFilterGUIComponent");
        JMeterVariableUtils.undoVariableReplacement(grpcSampler);
        Assert.assertEquals(grpcSampler.getDeadline(), ("5000"));
    }

    @Test
    public void testKeepSamplerValueWhenHasExceptionWithVariableReplace()
            throws NoSuchFieldException, IllegalAccessException {
        JMeterTreeListener jMeterTreeListener = new JMeterTreeListener();
        JMeterTreeModel jMeterTreeModel = new JMeterTreeModel();
        GuiPackage.initInstance(jMeterTreeListener, jMeterTreeModel);
        GuiPackage guiPackageInstance = GuiPackage.getInstance();
        Field field = guiPackageInstance.getClass().getDeclaredField("nodesToGui");
        field.setAccessible(true);
        field.set(guiPackageInstance, null);
        GRPCSampler grpcSampler = new GRPCSampler();
        grpcSampler.setDeadline("${deadline}");
        JMeterVariableUtils.undoVariableReplacement(grpcSampler);
        Assert.assertEquals(grpcSampler.getDeadline(), "${deadline}");
    }

    @Test
    public void testKeepSamplerValueWhenHasExceptionWithGetReplace()
            throws NoSuchFieldException, IllegalAccessException {
        JMeterTreeListener jMeterTreeListener = new JMeterTreeListener();
        JMeterTreeModel jMeterTreeModel = new JMeterTreeModel();
        GuiPackage.initInstance(jMeterTreeListener, jMeterTreeModel);
        GuiPackage guiPackageInstance = GuiPackage.getInstance();
        Field fieldTreeModel = guiPackageInstance.getClass().getDeclaredField("treeModel");
        Field fieldNodesToGui = guiPackageInstance.getClass().getDeclaredField("nodesToGui");
        fieldNodesToGui.setAccessible(true);
        fieldNodesToGui.set(guiPackageInstance, null);
        fieldTreeModel.setAccessible(true);
        fieldTreeModel.set(guiPackageInstance, null);
        GRPCSampler grpcSampler = new GRPCSampler();
        grpcSampler.setDeadline("${deadline}");
        JMeterVariableUtils.undoVariableReplacement(grpcSampler);
        Assert.assertEquals(grpcSampler.getDeadline(), "${deadline}");
    }

    @AfterMethod
    public void cleanGUIPackage() throws NoSuchFieldException, IllegalAccessException {
        GuiPackage guiPackageInstance = GuiPackage.getInstance();
        if (guiPackageInstance == null) return;
        Field field = guiPackageInstance.getClass().getDeclaredField("guiPack");
        field.setAccessible(true);
        field.set(guiPackageInstance, null);
    }

    public static class DummyGUIComponent implements JMeterGUIComponent {
        public DummyGUIComponent() {}

        @Override
        public void setName(String s) {}

        @Override
        public String getName() {
            return "DummyGUIComponent";
        }

        @Override
        public String getStaticLabel() {
            return "DummyGUIComponent";
        }

        @Override
        public String getLabelResource() {
            return "DummyGUIComponent";
        }

        @Override
        public String getDocAnchor() {
            return "DummyGUIComponent";
        }

        @Override
        public TestElement createTestElement() {
            Arguments arguments = new Arguments();
            arguments.addArgument("deadline", "5000");
            return arguments;
        }

        @Override
        public void modifyTestElement(TestElement testElement) {}

        @Override
        public boolean isEnabled() {
            return false;
        }

        @Override
        public void setEnabled(boolean b) {}

        @Override
        public JPopupMenu createPopupMenu() {
            return new JPopupMenu();
        }

        @Override
        public void configure(TestElement testElement) {}

        @Override
        public Collection<String> getMenuCategories() {
            return new ArrayList<>();
        }

        @Override
        public void clearGui() {}
    }

    public static class DummyNotMatchFilterGUIComponent implements JMeterGUIComponent {
        public DummyNotMatchFilterGUIComponent() {}

        @Override
        public void setName(String s) {}

        @Override
        public String getName() {
            return "DummyNotMatchFilterGUIComponent";
        }

        @Override
        public String getStaticLabel() {
            return "DummyNotMatchFilterGUIComponent";
        }

        @Override
        public String getLabelResource() {
            return "DummyNotMatchFilterGUIComponent";
        }

        @Override
        public String getDocAnchor() {
            return "DummyNotMatchFilterGUIComponent";
        }

        @Override
        public TestElement createTestElement() {
            return new GRPCSampler();
        }

        @Override
        public void modifyTestElement(TestElement testElement) {}

        @Override
        public boolean isEnabled() {
            return false;
        }

        @Override
        public void setEnabled(boolean b) {}

        @Override
        public JPopupMenu createPopupMenu() {
            return new JPopupMenu();
        }

        @Override
        public void configure(TestElement testElement) {}

        @Override
        public Collection<String> getMenuCategories() {
            return new ArrayList<>();
        }

        @Override
        public void clearGui() {}
    }
}
