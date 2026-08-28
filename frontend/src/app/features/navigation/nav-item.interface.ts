export interface NavItem {
  sectionName: string;
  icon: string;
  label: string;
  route?: string;
  onClick?: () => void;
  requiredRole?: string;
  children?: { label: string; route: string; requiredRole?: string }[];
}
