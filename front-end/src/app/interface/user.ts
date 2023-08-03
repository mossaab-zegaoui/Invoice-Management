export interface User {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  address?: string;
  phone?: string;
  title?: string;
  bio?: string;
  imageUrl?: string;
  notLocked: boolean;
  usingMfa: boolean;
  enabled: boolean;
  createdAt?: Date;
  roleName?: string;
  permission?: string;
}
