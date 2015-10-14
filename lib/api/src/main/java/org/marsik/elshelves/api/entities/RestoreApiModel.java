package org.marsik.elshelves.api.entities;

import gnu.trove.set.hash.THashSet;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RestoreApiModel extends BackupApiModel {
	Set<ProjectApiModel> projects = new THashSet<>();
}
