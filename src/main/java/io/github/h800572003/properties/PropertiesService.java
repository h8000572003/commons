package io.github.h800572003.properties;

import io.github.h800572003.exception.ApBusinessException;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class PropertiesService implements IPropertiesService {

    private final Map<String, IPropertieServiceHolder> map = new ConcurrentHashMap<>();
    private final List<Code> codes = new ArrayList<>();
    private List<PropertiesUpdateListener> propertiesUpdates = new ArrayList<>();

    @Override
    public void addListener(PropertiesUpdateListener propertiesUpdateListener) {
        this.propertiesUpdates.add(propertiesUpdateListener);
    }
    @Override
    public void addCode(String code, String name, IPropertieServiceHolder holder) {
        if (this.map.containsKey(code)) {
            throw new ApBusinessException("重複代碼{0}", code);
        }
        this.map.put(code, holder);
        final Code myCode = new Code();
        myCode.setKey(code);
        myCode.setValue(name);
        this.codes.add(myCode);
    }

    protected PropertiesService(IPropertiesRepository propertiesRepository) {
        super();
    }

    private List<IProperties> getCodeByDefault(String code, List<IProperties> def) {
        if (this.map.containsKey(code)) {
            return this.map.get(code).getProperties();
        } else {
            return def;
        }
    }

    @Override
    public List<IProperties> getPropertie(String code) {
        java.util.Objects.requireNonNull(code);
        return getCodeByDefault(code, Arrays.asList());
    }

    @Override
    public IProperties getValue(String code, String key) {
        java.util.Objects.requireNonNull(code);
        java.util.Objects.requireNonNull(key);
        if (this.map.containsKey(code)) {
            final Optional<IProperties> propertie = this.map.get(code).getPropertie(key);
            return propertie.orElseGet(MyProperties::new);
        } else {
            return new MyProperties();
        }
    }

    @Override
    public void saveOrUpdate(String category, IProperties properties) {
        this.propertiesUpdates.forEach(i -> i.update(category, properties));
        if (this.map.containsKey(category)) {
            this.map.get(category).saveOrUpdate(properties);
        }

    }

    @Override
    public void clear(String category) {
        updateAllOb(PropertiesUpdateStatus.CLEAR, category, null);
        if (this.map.containsKey(category)) {
            this.map.get(category).clear();
        }

    }

    @Override
    public void delete(String category, String code) {
        updateAllOb(PropertiesUpdateStatus.DELETE, category, code);
        if (this.map.containsKey(category)) {
            this.map.get(category).delete(code);
        }

    }

    @Override
    public List<Code> getCategory() {
        return this.codes;
    }

    @Override
    public void delete(String category) {
        updateAllOb(PropertiesUpdateStatus.DELETE, category, null);
        if (this.map.containsKey(category)) {
            this.map.get(category).delete();
        }

    }

    @Override
    public void clear() {
        codes.forEach(i -> this.clear(i.getKey()));
    }

    private void updateAllOb(PropertiesUpdateStatus delete, String category, String code) {
        this.propertiesUpdates.forEach(i -> i.update(delete, category, code));
    }

    @Override
    public void update() {
        this.clear();
        codes.forEach(this::loadProperties);

    }

    private void loadProperties(Code code) {
        this.getPropertie(code.getKey());
    }

}
