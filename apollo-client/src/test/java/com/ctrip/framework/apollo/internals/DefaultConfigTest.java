package com.ctrip.framework.apollo.internals;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import com.google.common.util.concurrent.SettableFuture;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigChangeListener;
import com.ctrip.framework.apollo.core.utils.ClassLoaderUtil;
import com.ctrip.framework.apollo.enums.PropertyChangeType;
import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class DefaultConfigTest {
  private File someResourceDir;
  private String someNamespace;
  private ConfigRepository configRepository;
  private Properties someProperties;

  @Before
  public void setUp() throws Exception {
    someResourceDir = new File(ClassLoaderUtil.getClassPath() + "/META-INF/config");
    someResourceDir.mkdirs();
    someNamespace = "someName";
    configRepository = mock(ConfigRepository.class);
  }

  @After
  public void tearDown() throws Exception {
    recursiveDelete(someResourceDir);
  }

  //helper method to clean created files
  private void recursiveDelete(File file) {
    if (!file.exists()) {
      return;
    }
    if (file.isDirectory()) {
      for (File f : file.listFiles()) {
        recursiveDelete(f);
      }
    }
    file.delete();
  }

  @Test
  public void testGetPropertyWithAllPropertyHierarchy() throws Exception {
    String someKey = "someKey";
    String someSystemPropertyValue = "system-property-value";

    String anotherKey = "anotherKey";
    String someLocalFileValue = "local-file-value";

    String lastKey = "lastKey";
    String someResourceValue = "resource-value";

    //set up system property
    System.setProperty(someKey, someSystemPropertyValue);

    //set up config repo
    someProperties = new Properties();
    someProperties.setProperty(someKey, someLocalFileValue);
    someProperties.setProperty(anotherKey, someLocalFileValue);
    when(configRepository.getConfig()).thenReturn(someProperties);

    //set up resource file
    File resourceFile = new File(someResourceDir, someNamespace + ".properties");
    Files.write(someKey + "=" + someResourceValue, resourceFile, Charsets.UTF_8);
    Files.append(System.getProperty("line.separator"), resourceFile, Charsets.UTF_8);
    Files.append(anotherKey + "=" + someResourceValue, resourceFile, Charsets.UTF_8);
    Files.append(System.getProperty("line.separator"), resourceFile, Charsets.UTF_8);
    Files.append(lastKey + "=" + someResourceValue, resourceFile, Charsets.UTF_8);

    DefaultConfig defaultConfig =
        new DefaultConfig(someNamespace, configRepository);

    String someKeyValue = defaultConfig.getProperty(someKey, null);
    String anotherKeyValue = defaultConfig.getProperty(anotherKey, null);
    String lastKeyValue = defaultConfig.getProperty(lastKey, null);

    //clean up
    System.clearProperty(someKey);

    assertEquals(someSystemPropertyValue, someKeyValue);
    assertEquals(someLocalFileValue, anotherKeyValue);
    assertEquals(someResourceValue, lastKeyValue);

  }

  @Test
  public void testGetIntProperty() throws Exception {
    String someStringKey = "someStringKey";
    String someStringValue = "someStringValue";

    String someKey = "someKey";
    Integer someValue = 2;

    Integer someDefaultValue = -1;

    //set up config repo
    someProperties = new Properties();
    someProperties.setProperty(someStringKey, someStringValue);
    someProperties.setProperty(someKey, String.valueOf(someValue));
    when(configRepository.getConfig()).thenReturn(someProperties);

    DefaultConfig defaultConfig =
        new DefaultConfig(someNamespace, configRepository);

    assertEquals(someValue, defaultConfig.getIntProperty(someKey, someDefaultValue));
    assertEquals(someDefaultValue, defaultConfig.getIntProperty(someStringKey, someDefaultValue));
  }

  @Test
  public void testGetLongProperty() throws Exception {
    String someStringKey = "someStringKey";
    String someStringValue = "someStringValue";

    String someKey = "someKey";
    Long someValue = 2l;

    Long someDefaultValue = -1l;

    //set up config repo
    someProperties = new Properties();
    someProperties.setProperty(someStringKey, someStringValue);
    someProperties.setProperty(someKey, String.valueOf(someValue));
    when(configRepository.getConfig()).thenReturn(someProperties);

    DefaultConfig defaultConfig =
        new DefaultConfig(someNamespace, configRepository);

    assertEquals(someValue, defaultConfig.getLongProperty(someKey, someDefaultValue));
    assertEquals(someDefaultValue, defaultConfig.getLongProperty(someStringKey, someDefaultValue));
  }

  @Test
  public void testGetShortProperty() throws Exception {
    String someStringKey = "someStringKey";
    String someStringValue = "someStringValue";

    String someKey = "someKey";
    Short someValue = 2;

    Short someDefaultValue = -1;

    //set up config repo
    someProperties = new Properties();
    someProperties.setProperty(someStringKey, someStringValue);
    someProperties.setProperty(someKey, String.valueOf(someValue));
    when(configRepository.getConfig()).thenReturn(someProperties);

    DefaultConfig defaultConfig =
        new DefaultConfig(someNamespace, configRepository);

    assertEquals(someValue, defaultConfig.getShortProperty(someKey, someDefaultValue));
    assertEquals(someDefaultValue, defaultConfig.getShortProperty(someStringKey, someDefaultValue));
  }

  @Test
  public void testGetFloatProperty() throws Exception {
    String someStringKey = "someStringKey";
    String someStringValue = "someStringValue";

    String someKey = "someKey";
    Float someValue = 2.20f;

    Float someDefaultValue = -1f;

    //set up config repo
    someProperties = new Properties();
    someProperties.setProperty(someStringKey, someStringValue);
    someProperties.setProperty(someKey, String.valueOf(someValue));
    when(configRepository.getConfig()).thenReturn(someProperties);

    DefaultConfig defaultConfig =
        new DefaultConfig(someNamespace, configRepository);

    assertEquals(someValue, defaultConfig.getFloatProperty(someKey, someDefaultValue), 0.001);
    assertEquals(someDefaultValue, defaultConfig.getFloatProperty(someStringKey, someDefaultValue), 0.001);
  }

  @Test
  public void testGetDoubleProperty() throws Exception {
    String someStringKey = "someStringKey";
    String someStringValue = "someStringValue";

    String someKey = "someKey";
    Double someValue = 2.20d;

    Double someDefaultValue = -1d;

    //set up config repo
    someProperties = new Properties();
    someProperties.setProperty(someStringKey, someStringValue);
    someProperties.setProperty(someKey, String.valueOf(someValue));
    when(configRepository.getConfig()).thenReturn(someProperties);

    DefaultConfig defaultConfig =
        new DefaultConfig(someNamespace, configRepository);

    assertEquals(someValue, defaultConfig.getDoubleProperty(someKey, someDefaultValue), 0.001);
    assertEquals(someDefaultValue, defaultConfig.getDoubleProperty(someStringKey, someDefaultValue), 0.001);
  }

  @Test
  public void testGetByteProperty() throws Exception {
    String someStringKey = "someStringKey";
    String someStringValue = "someStringValue";

    String someKey = "someKey";
    Byte someValue = 10;

    Byte someDefaultValue = -1;

    //set up config repo
    someProperties = new Properties();
    someProperties.setProperty(someStringKey, someStringValue);
    someProperties.setProperty(someKey, String.valueOf(someValue));
    when(configRepository.getConfig()).thenReturn(someProperties);

    DefaultConfig defaultConfig =
        new DefaultConfig(someNamespace, configRepository);

    assertEquals(someValue, defaultConfig.getByteProperty(someKey, someDefaultValue));
    assertEquals(someDefaultValue, defaultConfig.getByteProperty(someStringKey, someDefaultValue));
  }

  @Test
  public void testGetBooleanProperty() throws Exception {
    String someStringKey = "someStringKey";
    String someStringValue = "someStringValue";

    String someKey = "someKey";
    Boolean someValue = true;

    Boolean someDefaultValue = false;

    //set up config repo
    someProperties = new Properties();
    someProperties.setProperty(someStringKey, someStringValue);
    someProperties.setProperty(someKey, String.valueOf(someValue));
    when(configRepository.getConfig()).thenReturn(someProperties);

    DefaultConfig defaultConfig =
        new DefaultConfig(someNamespace, configRepository);

    assertEquals(someValue, defaultConfig.getBooleanProperty(someKey, someDefaultValue));
    assertEquals(someDefaultValue, defaultConfig.getBooleanProperty(someStringKey, someDefaultValue));
  }

  @Test
  public void testGetArrayProperty() throws Exception {
    String someKey = "someKey";
    String someDelimiter = ",";
    String someInvalidDelimiter = "{";

    String[] values = new String[]{"a", "b", "c"};
    String someValue = Joiner.on(someDelimiter).join(values);

    String[] someDefaultValue = new String[]{"1", "2"};

    //set up config repo
    someProperties = new Properties();
    someProperties.setProperty(someKey, someValue);
    when(configRepository.getConfig()).thenReturn(someProperties);

    DefaultConfig defaultConfig =
        new DefaultConfig(someNamespace, configRepository);

    assertArrayEquals(values, defaultConfig.getArrayProperty(someKey, someDelimiter, someDefaultValue));
    assertArrayEquals(someDefaultValue, defaultConfig.getArrayProperty(someKey, someInvalidDelimiter, someDefaultValue));
  }

  @Test
  public void testGetDatePropertyWithFormat() throws Exception {
    Date someDefaultValue = new Date();

    Date shortDate = assembleDate(2016, 9, 28, 0, 0, 0, 0);
    Date mediumDate = assembleDate(2016, 9, 28, 15, 10, 10, 0);
    Date longDate = assembleDate(2016, 9, 28, 15, 10, 10, 123);

    //set up config repo
    someProperties = new Properties();
    someProperties.setProperty("shortDateProperty", "2016-09-28");
    someProperties.setProperty("mediumDateProperty", "2016-09-28 15:10:10");
    someProperties.setProperty("longDateProperty", "2016-09-28 15:10:10.123");
    someProperties.setProperty("stringProperty", "someString");
    when(configRepository.getConfig()).thenReturn(someProperties);

    DefaultConfig defaultConfig =
        new DefaultConfig(someNamespace, configRepository);

    checkDatePropertyWithFormat(defaultConfig, shortDate, "shortDateProperty", "yyyy-MM-dd", someDefaultValue);
    checkDatePropertyWithFormat(defaultConfig, mediumDate, "mediumDateProperty", "yyyy-MM-dd HH:mm:ss", someDefaultValue);
    checkDatePropertyWithFormat(defaultConfig, shortDate, "mediumDateProperty", "yyyy-MM-dd", someDefaultValue);
    checkDatePropertyWithFormat(defaultConfig, longDate, "longDateProperty", "yyyy-MM-dd HH:mm:ss.SSS", someDefaultValue);
    checkDatePropertyWithFormat(defaultConfig, mediumDate, "longDateProperty", "yyyy-MM-dd HH:mm:ss", someDefaultValue);
    checkDatePropertyWithFormat(defaultConfig, shortDate, "longDateProperty", "yyyy-MM-dd", someDefaultValue);
    checkDatePropertyWithFormat(defaultConfig, someDefaultValue, "stringProperty", "yyyy-MM-dd", someDefaultValue);
  }

  @Test
  public void testGetDatePropertyWithNoFormat() throws Exception {
    Date someDefaultValue = new Date();

    Date shortDate = assembleDate(2016, 9, 28, 0, 0, 0, 0);
    Date mediumDate = assembleDate(2016, 9, 28, 15, 10, 10, 0);
    Date longDate = assembleDate(2016, 9, 28, 15, 10, 10, 123);

    //set up config repo
    someProperties = new Properties();
    someProperties.setProperty("shortDateProperty", "2016-09-28");
    someProperties.setProperty("mediumDateProperty", "2016-09-28 15:10:10");
    someProperties.setProperty("longDateProperty", "2016-09-28 15:10:10.123");
    someProperties.setProperty("stringProperty", "someString");
    when(configRepository.getConfig()).thenReturn(someProperties);

    DefaultConfig defaultConfig =
        new DefaultConfig(someNamespace, configRepository);

    checkDatePropertyWithoutFormat(defaultConfig, shortDate, "shortDateProperty", someDefaultValue);
    checkDatePropertyWithoutFormat(defaultConfig, mediumDate, "mediumDateProperty", someDefaultValue);
    checkDatePropertyWithoutFormat(defaultConfig, longDate, "longDateProperty", someDefaultValue);
    checkDatePropertyWithoutFormat(defaultConfig, someDefaultValue, "stringProperty", someDefaultValue);
  }

  @Test
  public void testGetEnumProperty() throws Exception {
    SomeEnum someDefaultValue = SomeEnum.defaultValue;

    //set up config repo
    someProperties = new Properties();
    someProperties.setProperty("enumProperty", "someValue");
    someProperties.setProperty("stringProperty", "someString");
    when(configRepository.getConfig()).thenReturn(someProperties);

    DefaultConfig defaultConfig =
        new DefaultConfig(someNamespace, configRepository);

    assertEquals(SomeEnum.someValue, defaultConfig.getEnumProperty("enumProperty", SomeEnum.class, someDefaultValue));
    assertEquals(someDefaultValue, defaultConfig.getEnumProperty("stringProperty", SomeEnum.class, someDefaultValue));
  }

  @Test
  public void testGetDurationProperty() throws Exception {
    long someDefaultValue = 1000;
    long result = 2 * 24 * 3600 * 1000 + 3 * 3600 * 1000 + 4 * 60 * 1000 + 5 * 1000 + 123;

    //set up config repo
    someProperties = new Properties();
    someProperties.setProperty("durationProperty", "2D3H4m5S123ms");
    someProperties.setProperty("stringProperty", "someString");
    when(configRepository.getConfig()).thenReturn(someProperties);

    DefaultConfig defaultConfig =
        new DefaultConfig(someNamespace, configRepository);

    assertEquals(result, defaultConfig.getDurationProperty("durationProperty", someDefaultValue));
    assertEquals(someDefaultValue, defaultConfig.getDurationProperty("stringProperty", someDefaultValue));
  }

  @Test
  public void testOnRepositoryChange() throws Exception {
    String someKey = "someKey";
    String someSystemPropertyValue = "system-property-value";

    String anotherKey = "anotherKey";
    String someLocalFileValue = "local-file-value";

    String keyToBeDeleted = "keyToBeDeleted";
    String keyToBeDeletedValue = "keyToBeDeletedValue";

    String yetAnotherKey = "yetAnotherKey";
    String yetAnotherValue = "yetAnotherValue";

    String yetAnotherResourceValue = "yetAnotherResourceValue";
    //set up system property
    System.setProperty(someKey, someSystemPropertyValue);

    //set up config repo
    someProperties = new Properties();
    someProperties.putAll(ImmutableMap
        .of(someKey, someLocalFileValue, anotherKey, someLocalFileValue, keyToBeDeleted,
            keyToBeDeletedValue, yetAnotherKey, yetAnotherValue));
    when(configRepository.getConfig()).thenReturn(someProperties);

    //set up resource file
    File resourceFile = new File(someResourceDir, someNamespace + ".properties");
    Files.append(yetAnotherKey + "=" + yetAnotherResourceValue, resourceFile, Charsets.UTF_8);

    DefaultConfig defaultConfig =
        new DefaultConfig(someNamespace, configRepository);

    final SettableFuture<ConfigChangeEvent> configChangeFuture = SettableFuture.create();
    ConfigChangeListener someListener = new ConfigChangeListener() {
      @Override
      public void onChange(ConfigChangeEvent changeEvent) {
        configChangeFuture.set(changeEvent);
      }
    };

    defaultConfig.addChangeListener(someListener);

    Properties newProperties = new Properties();
    String someKeyNewValue = "new-some-value";
    String anotherKeyNewValue = "another-new-value";
    String newKey = "newKey";
    String newValue = "newValue";
    newProperties.putAll(ImmutableMap
        .of(someKey, someKeyNewValue, anotherKey, anotherKeyNewValue, newKey, newValue));

    defaultConfig.onRepositoryChange(someNamespace, newProperties);

    ConfigChangeEvent changeEvent = configChangeFuture.get(500, TimeUnit.MILLISECONDS);

    //clean up
    System.clearProperty(someKey);

    assertEquals(someNamespace, changeEvent.getNamespace());
    assertEquals(4, changeEvent.changedKeys().size());

    ConfigChange anotherKeyChange = changeEvent.getChange(anotherKey);
    assertEquals(someLocalFileValue, anotherKeyChange.getOldValue());
    assertEquals(anotherKeyNewValue, anotherKeyChange.getNewValue());
    assertEquals(PropertyChangeType.MODIFIED, anotherKeyChange.getChangeType());

    ConfigChange yetAnotherKeyChange = changeEvent.getChange(yetAnotherKey);
    assertEquals(yetAnotherValue, yetAnotherKeyChange.getOldValue());
    assertEquals(yetAnotherResourceValue, yetAnotherKeyChange.getNewValue());
    assertEquals(PropertyChangeType.MODIFIED, yetAnotherKeyChange.getChangeType());

    ConfigChange keyToBeDeletedChange = changeEvent.getChange(keyToBeDeleted);
    assertEquals(keyToBeDeletedValue, keyToBeDeletedChange.getOldValue());
    assertEquals(null, keyToBeDeletedChange.getNewValue());
    assertEquals(PropertyChangeType.DELETED, keyToBeDeletedChange.getChangeType());

    ConfigChange newKeyChange = changeEvent.getChange(newKey);
    assertEquals(null, newKeyChange.getOldValue());
    assertEquals(newValue, newKeyChange.getNewValue());
    assertEquals(PropertyChangeType.ADDED, newKeyChange.getChangeType());
  }

  private void checkDatePropertyWithFormat(Config config, Date expected, String propertyName, String format, Date defaultValue) {
    assertEquals(expected, config.getDateProperty(propertyName, format, defaultValue));
  }

  private void checkDatePropertyWithoutFormat(Config config, Date expected, String propertyName, Date defaultValue) {
    assertEquals(expected, config.getDateProperty(propertyName, defaultValue));
  }

  private Date assembleDate(int year, int month, int day, int hour, int minute, int second, int millisecond) {
    Calendar date = Calendar.getInstance();
    date.set(year, month - 1, day, hour, minute, second); //Month in Calendar is 0 based
    date.set(Calendar.MILLISECOND, millisecond);

    return date.getTime();
  }

  private enum SomeEnum {
    someValue, defaultValue
  }
}