package org.kevoree.registry.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.kevoree.registry.config.Constants;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DeployUnit.
 */
@Entity
@Table(name = "T_DEPLOY_UNIT")
public class DeployUnit extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(min = 1)
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Pattern(regexp = Constants.DU_VERSION_REGEX)
    @Size(min = 1, max = 50)
    @Column(name = "version", nullable = false, length = 50)
    private String version;

    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "platform", length = 50)
    private String platform;

    @ManyToOne
    @JoinColumn(name="typedefinition_id")
    @JsonIgnoreProperties({ "nbdownloads", "deployUnits" })
    private TypeDefinition typeDefinition;

    @NotNull
    @Column(name = "model")
    @Lob
    private String model;

    @NotNull
    @Column(name = "nbdownloads")
    private Long nbDownloads = 0L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Long getNbDownloads() {
        return nbDownloads;
    }

    public void setNbDownloads(Long nbDownloads) {
        this.nbDownloads = nbDownloads;
    }

    public TypeDefinition getTypeDefinition() {
        return this.typeDefinition;
    }

    public void setTypeDefinition(TypeDefinition tdef) {
        this.typeDefinition = tdef;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DeployUnit deployUnit = (DeployUnit) o;
        if(deployUnit.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, deployUnit.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "DeployUnit{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", version='" + version + "'" +
            ", platform='" + platform + "'" +
//            ", typeDefinitionId=" + typeDefinition.getId() +
            ", nbDownloads=" + nbDownloads +
            ", model='" + model + "'" +
            ", createdBy='" + getCreatedBy() + '\'' +
            ", createdDate='" + getCreatedDate()+ '\'' +
            ", lastModifiedBy='" + getLastModifiedBy()+ '\'' +
            ", lastModifiedDate='" + getLastModifiedDate()+ '\'' +
            '}';
    }
}
