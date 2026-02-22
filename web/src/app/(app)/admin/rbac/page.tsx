"use client";

import { useMemo, useState } from "react";
import { PageHeader } from "@/components/page-header";
import { Card, CardContent } from "@/components/ui/card";
import { PermissionGate } from "@/components/permission-gate";
import { DataTable } from "@/components/data-table";
import { Button } from "@/components/ui/button";
import { Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Checkbox } from "@/components/ui/checkbox";
import {
  useAdminPermissions,
  useAdminRoles,
  useAdminUsers,
  useCreateRole,
  useDeleteRole,
  useRolePermissions,
  useUpdateRole,
  useUpdateRolePermissions,
  useUpdateUserRoles,
  useUserRoles,
  type Permission,
  type Role,
  type UserSummary,
} from "@/hooks/api-hooks";
import { ConfirmDialog } from "@/components/confirm-dialog";
import { ListToolbar } from "@/components/list-toolbar";

export default function RbacPage() {
  const rolesQuery = useAdminRoles();
  const permissionsQuery = useAdminPermissions();
  const createRoleMutation = useCreateRole();
  const deleteRoleMutation = useDeleteRole();

  const [selectedRoleId, setSelectedRoleId] = useState<string | null>(null);
  const [roleDialogOpen, setRoleDialogOpen] = useState(false);
  const [roleDialogMode, setRoleDialogMode] = useState<"create" | "edit">("create");
  const [roleCode, setRoleCode] = useState("");
  const [roleName, setRoleName] = useState("");
  const [deleteRoleId, setDeleteRoleId] = useState<string | null>(null);

  const selectedRole = useMemo(
    () => rolesQuery.data?.find((role) => role.id === selectedRoleId) ?? null,
    [rolesQuery.data, selectedRoleId]
  );

  const rolePermissionsQuery = useRolePermissions(selectedRoleId ?? undefined);
  const updateRolePermissionsMutation = useUpdateRolePermissions(selectedRoleId ?? undefined);

  const [draftSelectedPerms, setDraftSelectedPerms] = useState<string[]>([]);
  const [rolePermsDirty, setRolePermsDirty] = useState(false);

  const [userPage, setUserPage] = useState(0);
  const [userSearch, setUserSearch] = useState("");
  const usersQuery = useAdminUsers({ page: userPage, size: 10, search: userSearch });
  const [selectedUserId, setSelectedUserId] = useState<string | null>(null);
  const userRolesQuery = useUserRoles(selectedUserId ?? undefined);
  const updateUserRolesMutation = useUpdateUserRoles(selectedUserId ?? undefined);
  const [draftSelectedUserRoles, setDraftSelectedUserRoles] = useState<string[]>([]);
  const [userRolesDirty, setUserRolesDirty] = useState(false);

  const updateRoleMutation = useUpdateRole(selectedRoleId ?? "");

  const openCreateRole = () => {
    setRoleDialogMode("create");
    setRoleCode("");
    setRoleName("");
    setRoleDialogOpen(true);
  };

  const openEditRole = (role: { code: string; name: string; id: string }) => {
    setRoleDialogMode("edit");
    setRoleCode(role.code);
    setRoleName(role.name);
    setSelectedRoleId(role.id);
    setRoleDialogOpen(true);
  };

  const selectRole = (roleId: string) => {
    setSelectedRoleId(roleId);
    setRolePermsDirty(false);
    setDraftSelectedPerms([]);
  };

  const handleSaveRole = () => {
    if (roleDialogMode === "create") {
      createRoleMutation.mutate({ code: roleCode, name: roleName }, { onSuccess: () => setRoleDialogOpen(false) });
      return;
    }
    updateRoleMutation.mutate({ code: roleCode, name: roleName }, { onSuccess: () => setRoleDialogOpen(false) });
  };

  const selectedPerms = rolePermsDirty ? draftSelectedPerms : (rolePermissionsQuery.data ?? []);
  const selectedUserRoles = userRolesDirty ? draftSelectedUserRoles : (userRolesQuery.data ?? []);

  return (
    <PermissionGate permission="SYS_ADMIN">
      <div className="space-y-6">
        <PageHeader title="RBAC Admin" description="Manage roles and permissions" />

        <Tabs defaultValue="roles" className="space-y-4">
          <TabsList>
            <TabsTrigger value="roles">Roles & Permissions</TabsTrigger>
            <TabsTrigger value="users">User Roles</TabsTrigger>
          </TabsList>

          <TabsContent value="roles">
            <div className="grid gap-6 lg:grid-cols-[360px_1fr]">
              <Card className="shadow-sm">
                <CardContent className="pt-6 space-y-3">
                  <div className="flex items-center justify-between">
                    <div className="text-sm font-semibold">Roles</div>
                    <Button size="sm" onClick={openCreateRole}>
                      Create Role
                    </Button>
                  </div>

                  <DataTable
                    columns={[
                      { key: "code", header: "Code" },
                      { key: "name", header: "Name" },
                      {
                        key: "actions",
                        header: "Actions",
                        align: "right",
                        render: (row: Role) => (
                          <div className="flex items-center justify-end gap-2">
                            <Button size="sm" variant="outline" onClick={() => selectRole(row.id)}>
                              Select
                            </Button>
                            <Button size="sm" variant="secondary" onClick={() => openEditRole(row)}>
                              Edit
                            </Button>
                            <Button size="sm" variant="destructive" onClick={() => setDeleteRoleId(row.id)}>
                              Delete
                            </Button>
                          </div>
                        ),
                      },
                    ]}
                    data={rolesQuery.data ?? []}
                    loading={rolesQuery.isLoading}
                    page={0}
                    size={10}
                    total={(rolesQuery.data ?? []).length}
                    onPageChange={() => {}}
                    emptyTitle="No roles"
                    emptyDescription="Create a role to start assigning permissions."
                    emptyAction={<Button onClick={openCreateRole}>Create Role</Button>}
                  />
                </CardContent>
              </Card>

              <Card className="shadow-sm">
                <CardContent className="pt-6 space-y-4">
                  <div className="text-sm font-semibold">Assign Permissions</div>
                  {selectedRole ? (
                    <>
                      <div className="text-xs text-muted-foreground">
                        Editing role: {selectedRole.name} ({selectedRole.code})
                      </div>
                      <div className="grid gap-2 md:grid-cols-2">
                        {(permissionsQuery.data ?? []).map((perm: Permission) => (
                          <label key={perm.code} className="flex items-center gap-2 text-sm">
                            <Checkbox
                              checked={selectedPerms.includes(perm.code)}
                              onCheckedChange={(checked) => {
                                setRolePermsDirty(true);
                                const current = selectedPerms;
                                if (checked) {
                                  setDraftSelectedPerms(
                                    current.includes(perm.code) ? current : [...current, perm.code]
                                  );
                                } else {
                                  setDraftSelectedPerms(current.filter((code) => code !== perm.code));
                                }
                              }}
                            />
                            <span>{perm.code}</span>
                          </label>
                        ))}
                      </div>
                      <div>
                        <Button
                          onClick={() =>
                            updateRolePermissionsMutation.mutate(selectedPerms, {
                              onSuccess: () => {
                                setRolePermsDirty(false);
                                setDraftSelectedPerms([]);
                              },
                            })
                          }
                          disabled={!selectedRoleId}
                        >
                          Save Permissions
                        </Button>
                      </div>
                    </>
                  ) : (
                    <div className="text-sm text-muted-foreground">Select a role to assign permissions.</div>
                  )}
                </CardContent>
              </Card>
            </div>
          </TabsContent>

          <TabsContent value="users">
            <div className="grid gap-6 lg:grid-cols-[420px_1fr]">
              <Card className="shadow-sm">
                <CardContent className="pt-6 space-y-3">
                  <div className="text-sm font-semibold">Users</div>
                  <ListToolbar
                    search={userSearch}
                    onSearchChange={(value) => {
                      setUserSearch(value);
                      setUserPage(0);
                    }}
                    onReset={() => {
                      setUserSearch("");
                      setUserPage(0);
                    }}
                  />
                  <DataTable
                    columns={[
                      { key: "fullName", header: "Name" },
                      { key: "email", header: "Email" },
                      {
                        key: "actions",
                        header: "Actions",
                        align: "right",
                        render: (row: UserSummary) => (
                          <div className="flex items-center justify-end">
                            <Button size="sm" variant="outline" onClick={() => {
                              setSelectedUserId(row.id);
                              setUserRolesDirty(false);
                              setDraftSelectedUserRoles([]);
                            }}>
                              Select
                            </Button>
                          </div>
                        ),
                      },
                    ]}
                    data={usersQuery.data?.content ?? []}
                    loading={usersQuery.isLoading}
                    page={userPage}
                    size={10}
                    total={usersQuery.data?.totalElements ?? 0}
                    onPageChange={setUserPage}
                    emptyTitle="No users"
                    emptyDescription="No users found in this tenant."
                  />
                </CardContent>
              </Card>

              <Card className="shadow-sm">
                <CardContent className="pt-6 space-y-4">
                  <div className="text-sm font-semibold">Assign Roles</div>
                  {selectedUserId ? (
                    <>
                      <div className="grid gap-2">
                        {(rolesQuery.data ?? []).map((role: Role) => (
                          <label key={role.id} className="flex items-center gap-2 text-sm">
                            <Checkbox
                              checked={selectedUserRoles.includes(role.id)}
                              onCheckedChange={(checked) => {
                                setUserRolesDirty(true);
                                const current = selectedUserRoles;
                                if (checked) {
                                  setDraftSelectedUserRoles(current.includes(role.id) ? current : [...current, role.id]);
                                } else {
                                  setDraftSelectedUserRoles(current.filter((id) => id !== role.id));
                                }
                              }}
                            />
                            <span>
                              {role.name} ({role.code})
                            </span>
                          </label>
                        ))}
                      </div>
                      <Button
                        onClick={() =>
                          updateUserRolesMutation.mutate(selectedUserRoles, {
                            onSuccess: () => {
                              setUserRolesDirty(false);
                              setDraftSelectedUserRoles([]);
                            },
                          })
                        }
                      >
                        Save User Roles
                      </Button>
                    </>
                  ) : (
                    <div className="text-sm text-muted-foreground">Select a user to assign roles.</div>
                  )}
                </CardContent>
              </Card>
            </div>
          </TabsContent>
        </Tabs>

        <Dialog open={roleDialogOpen} onOpenChange={setRoleDialogOpen}>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>{roleDialogMode === "create" ? "Create Role" : "Edit Role"}</DialogTitle>
            </DialogHeader>
            <div className="space-y-3">
              <Input placeholder="Code" value={roleCode} onChange={(e) => setRoleCode(e.target.value)} />
              <Input placeholder="Name" value={roleName} onChange={(e) => setRoleName(e.target.value)} />
            </div>
            <DialogFooter>
              <Button variant="ghost" onClick={() => setRoleDialogOpen(false)}>
                Cancel
              </Button>
              <Button onClick={handleSaveRole} disabled={!roleCode || !roleName}>
                Save
              </Button>
            </DialogFooter>
          </DialogContent>
        </Dialog>

        <ConfirmDialog
          open={!!deleteRoleId}
          title="Delete role?"
          description="This action cannot be undone."
          onCancel={() => setDeleteRoleId(null)}
          onConfirm={() => deleteRoleId && deleteRoleMutation.mutate(deleteRoleId, { onSuccess: () => setDeleteRoleId(null) })}
          confirmLabel="Delete"
        />
      </div>
    </PermissionGate>
  );
}
